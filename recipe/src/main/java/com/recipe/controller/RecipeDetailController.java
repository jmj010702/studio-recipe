package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.ResponseLikeStatus;
import com.recipe.exceptions.recipe.RecipeException;
import com.recipe.service.RecipeService;
import com.recipe.service.LikeService;
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
    
    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeDetail(@PathVariable Long recipeId) {
        log.info("GET /api/details/{} - 레시피 상세 조회", recipeId);
        
        try {
            // 현재 로그인된 사용자 정보 가져오기
            String username = getCurrentUsername();
            Long userId = getCurrentUserId();
            
            // 레시피 정보 조회
            RecipeResponseDTO recipe = recipeService.findOneRecipe(recipeId, userId);
            
            // 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("recipe", recipe);
            
            // 로그인한 사용자라면 좋아요 상태 추가
            if (username != null) {
                boolean isLiked = likeService.isLiked(username, recipeId);
                data.put("isLiked", isLiked);
            } else {
                data.put("isLiked", false);
            }
            
            Map<String, Object> response = Map.of("data", data);
            
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
    
    @PostMapping("/likes")
    public ResponseEntity<?> toggleLike(@RequestParam("recipe_id") Long recipeId) {
        log.info("POST /api/details/likes - 레시피 좋아요 토글: {}", recipeId);
        
        try {
            String username = getCurrentUsername();
            
            if (username == null) {
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // 좋아요 토글
            LikeService.LikeResult result = likeService.toggleLike(username, recipeId);
            
            return ResponseEntity.ok(Map.of(
                "message", result.isLiked() ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.",
                "isLiked", result.isLiked(),
                "likeCount", result.getLikeCount()
            ));
            
        } catch (Exception e) {
            log.error("좋아요 처리 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "좋아요 처리에 실패했습니다."));
        }
    }
    
    @PostMapping("/completion")
    public ResponseEntity<?> completeRecipe(@RequestParam("recipe_id") Long recipeId) {
        log.info("POST /api/details/completion - 레시피 완료: {}", recipeId);
        
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.status(401)
                        .body(Map.of(
                            "error", "로그인이 필요합니다.",
                            "loginRequired", true
                        ));
            }
            
            // TODO: 완료 로직 구현
            // referenceService.userRecipeComplete(recipeId, userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "레시피 완료 처리되었습니다.",
                "recipeId", recipeId
            ));
            
        } catch (Exception e) {
            log.error("완료 처리 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "완료 처리에 실패했습니다."));
        }
    }
    
    /**
     * 현재 로그인된 사용자의 username을 가져옵니다.
     * 로그인하지 않은 경우 null을 반환합니다.
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || 
                !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.recipe.domain.dto.auth.CustomerDetails) {
                com.recipe.domain.dto.auth.CustomerDetails userDetails = 
                    (com.recipe.domain.dto.auth.CustomerDetails) principal;
                return userDetails.getUsername();  // "namgyu2001"
            }
            
            return null;
            
        } catch (Exception e) {
            log.warn("사용자 인증 정보 조회 실패", e);
            return null;
        }
    }
    
    /**
     * 현재 로그인된 사용자의 ID를 가져옵니다.
     * 로그인하지 않은 경우 null을 반환합니다.
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || 
                !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.recipe.domain.dto.auth.CustomerDetails) {
                com.recipe.domain.dto.auth.CustomerDetails userDetails = 
                    (com.recipe.domain.dto.auth.CustomerDetails) principal;
                return userDetails.getUserId();
            }
            
            return null;
            
        } catch (Exception e) {
            log.warn("사용자 인증 정보 조회 실패", e);
            return null;
        }
    }
}