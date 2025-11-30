package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.exceptions.recipe.RecipeException;
import com.recipe.service.RecipeService;
import com.recipe.service.LikeService;
import com.recipe.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/details")
@RequiredArgsConstructor
@Log4j2
public class RecipeDetailController {
    
    private final RecipeService recipeService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    
    /**
     * 레시피 상세 조회 (공개 - 인증 불필요)
     * GET /api/details/{recipeId}
     */
    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeDetail(@PathVariable Long recipeId) {
        log.info("GET /api/details/{} - 레시피 상세 조회", recipeId);
        
        try {
            // 현재 로그인된 사용자 정보 가져오기 (없으면 null)
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();
            
            // 레시피 정보 조회
            RecipeResponseDTO recipe = recipeService.findOneRecipe(recipeId, userId);
            
            // 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("recipe", recipe);
            
            // 로그인한 사용자라면 좋아요/북마크 상태 + 작성자 여부 추가
            if (username != null && userId != null) {
                boolean isLiked = likeService.isLiked(username, recipeId);
                boolean isBookmarked = bookmarkService.isBookmarked(username, recipeId);
                // userId 필드는 DTO에 추가했으므로 사용 가능
                boolean isMyRecipe = recipe.getUserId() != null && recipe.getUserId().equals(userId);
                
                data.put("isLiked", isLiked);
                data.put("isBookmarked", isBookmarked);
                data.put("isMyRecipe", isMyRecipe);  
            } else {
                data.put("isLiked", false);
                data.put("isBookmarked", false);
                data.put("isMyRecipe", false);
            }
            
            Map<String, Object> response = Map.of("data", data);
            
            // ⚠️ [중요] 다시 getRcpTtl()로 되돌렸습니다. (DTO 필드명 rcpTtl과 일치)
            log.info("레시피 상세 조회 완료 - ID: {}, 제목: {}", recipeId, recipe.getRcpTtl());
            
            return ResponseEntity.ok(response);
            
        } catch (RecipeException e) {
            log.error("레시피를 찾을 수 없음 - ID: {}, 코드: {}", recipeId, e.getCode(), e);
            return ResponseEntity.status(e.getCode())
                    .body(Map.of(
                        "error", e.getMsg(),
                        "recipeId", recipeId,
                        "code", e.getCode()
                    ));
        } catch (Exception e) {
            log.error("레시피 상세 조회 실패 - ID: {}", recipeId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 레시피 삭제 (인증 필수 - 작성자만 가능)
     * DELETE /api/details/{recipeId}
     */
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeId) {
        log.info("DELETE /api/details/{} - 레시피 삭제 요청", recipeId);
        
        try {
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();
            
            // 인증 여부 확인
            if (username == null || userId == null) {
                log.warn("인증되지 않은 사용자의 레시피 삭제 요청");
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // 레시피 삭제 처리 (Service에서 권한 확인 및 삭제 수행)
            recipeService.deleteRecipe(recipeId, userId);
            
            log.info("레시피 삭제 완료 - userId: {}, recipeId: {}", userId, recipeId);
            
            return ResponseEntity.ok(Map.of(
                "message", "레시피가 삭제되었습니다.",
                "success", true,
                "recipeId", recipeId
            ));
            
        } catch (RecipeException e) {
            log.error("레시피 삭제 실패 - recipeId: {}, 코드: {}", recipeId, e.getCode(), e);
            return ResponseEntity.status(e.getCode())
                    .body(Map.of(
                        "error", e.getMsg(),
                        "code", e.getCode()
                    ));
        } catch (IllegalArgumentException e) {
            log.error("권한 없음 - recipeId: {}, message: {}", recipeId, e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("레시피 삭제 처리 실패 - recipeId: {}", recipeId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "레시피 삭제에 실패했습니다."));
        }
    }
    
    /**
     * 레시피 좋아요 토글 (인증 필수)
     * POST /api/details/likes?recipe_id={recipeId}
     */
    @PostMapping("/likes")
    public ResponseEntity<?> toggleLike(@RequestParam("recipe_id") Long recipeId) {
        log.info("POST /api/details/likes - 레시피 좋아요 토글: {}", recipeId);
        
        try {
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();
            
            // 인증 여부 확인
            if (username == null || userId == null) {
                log.warn("인증되지 않은 사용자의 좋아요 요청");
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // 좋아요 토글 처리
            LikeService.LikeResult result = likeService.toggleLike(username, recipeId);
            
            log.info("좋아요 토글 완료 - username: {}, recipeId: {}, isLiked: {}", 
                    username, recipeId, result.isLiked());
            
            return ResponseEntity.ok(Map.of(
                "message", result.isLiked() ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.",
                "isLiked", result.isLiked(),
                "likeCount", result.getLikeCount(),
                "recipeId", recipeId
            ));
            
        } catch (RecipeException e) {
            log.error("레시피 좋아요 실패 - recipeId: {}, 코드: {}", recipeId, e.getCode(), e);
            return ResponseEntity.status(e.getCode())
                    .body(Map.of(
                        "error", e.getMsg(),
                        "code", e.getCode()
                    ));
        } catch (Exception e) {
            log.error("좋아요 처리 실패 - recipeId: {}", recipeId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "좋아요 처리에 실패했습니다."));
        }
    }
    
    /**
     * 레시피 북마크(찜) 토글 (인증 필수)
     * POST /api/details/bookmarks?recipe_id={recipeId}
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<?> toggleBookmark(@RequestParam("recipe_id") Long recipeId) {
        log.info("POST /api/details/bookmarks - 레시피 북마크 토글: {}", recipeId);
        
        try {
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();
            
            // 인증 여부 확인
            if (username == null || userId == null) {
                log.warn("인증되지 않은 사용자의 북마크 요청");
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // 북마크 토글 처리
            BookmarkService.BookmarkResult result = bookmarkService.toggleBookmark(username, recipeId);
            
            log.info("북마크 토글 완료 - username: {}, recipeId: {}, isBookmarked: {}", 
                    username, recipeId, result.isBookmarked());
            
            return ResponseEntity.ok(Map.of(
                "message", result.isBookmarked() ? "레시피를 찜했습니다." : "찜을 취소했습니다.",
                "isBookmarked", result.isBookmarked(),
                "recipeId", recipeId
            ));
            
        } catch (RecipeException e) {
            log.error("레시피 북마크 실패 - recipeId: {}, 코드: {}", recipeId, e.getCode(), e);
            return ResponseEntity.status(e.getCode())
                    .body(Map.of(
                        "error", e.getMsg(),
                        "code", e.getCode()
                    ));
        } catch (Exception e) {
            log.error("북마크 처리 실패 - recipeId: {}", recipeId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "북마크 처리에 실패했습니다."));
        }
    }
    
    /**
     * 레시피 완료 기록 (인증 필수)
     * POST /api/details/completion?recipe_id={recipeId}
     */
    @PostMapping("/completion")
    public ResponseEntity<?> completeRecipe(@RequestParam("recipe_id") Long recipeId) {
        log.info("POST /api/details/completion - 레시피 완료 기록: {}", recipeId);
        
        try {
            Long userId = getCurrentUserId();
            String username = getCurrentUsername();
            
            // 인증 여부 확인
            if (userId == null || username == null) {
                log.warn("인증되지 않은 사용자의 완료 요청");
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // 완료 로직 구현 (아래 주석 해제)
            // referenceService.userRecipeComplete(recipeId, userId);
            
            log.info("레시피 완료 기록 완료 - userId: {}, recipeId: {}", userId, recipeId);
            
            return ResponseEntity.ok(Map.of(
                "message", "레시피 완료 처리되었습니다.",
                "recipeId", recipeId,
                "userId", userId
            ));
            
        } catch (RecipeException e) {
            log.error("레시피 완료 실패 - recipeId: {}, 코드: {}", recipeId, e.getCode(), e);
            return ResponseEntity.status(e.getCode())
                    .body(Map.of(
                        "error", e.getMsg(),
                        "code", e.getCode()
                    ));
        } catch (Exception e) {
            log.error("완료 처리 실패 - recipeId: {}", recipeId, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "완료 처리에 실패했습니다."));
        }
    }
    
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomerDetails) {
                return ((CustomerDetails) principal).getUsername();
            }
            if (principal instanceof String) {
                return (String) principal;
            }
            return null;
        } catch (Exception e) {
            log.warn("사용자명 조회 실패", e);
            return null;
        }
    }
    
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomerDetails) {
                return ((CustomerDetails) principal).getUserId();
            }
            return null;
        } catch (Exception e) {
            log.warn("사용자 ID 조회 실패", e);
            return null;
        }
    }
}