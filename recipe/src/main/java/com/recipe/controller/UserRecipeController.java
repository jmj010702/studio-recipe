package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeSimpleDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.service.UserRecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserRecipeController {
    
    @Autowired
    private UserRecipeService userRecipeService;
    
    // ========== 좋아요 관련 API ==========
    
    /**
     * 좋아요한 레시피 목록 조회
     */
    @GetMapping("/liked-recipes")
    public ResponseEntity<?> getLikedRecipes(@AuthenticationPrincipal CustomerDetails user) {
        try {
            List<RecipeSimpleDTO> likedRecipes = userRecipeService.getLikedRecipes(user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", likedRecipes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("좋아요 레시피 조회 실패", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "좋아요한 레시피를 불러올 수 없습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 레시피 좋아요 추가
     */
    @PostMapping("/liked-recipes/{rcpSno}")
    public ResponseEntity<?> addLike(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        try {
            userRecipeService.addLike(user.getUsername(), rcpSno);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "좋아요 추가 완료");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            // 이미 좋아요한 경우
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 존재하지 않는 사용자/레시피
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("좋아요 추가 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "좋아요 추가 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 레시피 좋아요 취소
     */
    @DeleteMapping("/liked-recipes/{rcpSno}")
    public ResponseEntity<?> removeLike(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        try {
            userRecipeService.removeLike(user.getUsername(), rcpSno);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "좋아요 취소 완료");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("좋아요 취소 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "좋아요 취소 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 레시피 좋아요 여부 확인
     */
    @GetMapping("/liked-recipes/{rcpSno}/status")
    public ResponseEntity<?> checkLikeStatus(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        boolean isLiked = userRecipeService.isLiked(user.getUsername(), rcpSno);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        
        return ResponseEntity.ok(response);
    }
    
    // ========== 북마크(찜) 관련 API ==========
    
    /**
     * 북마크한 레시피 목록 조회
     */
    @GetMapping("/bookmarked-recipes")
    public ResponseEntity<?> getBookmarkedRecipes(@AuthenticationPrincipal CustomerDetails user) {
        try {
            List<RecipeSimpleDTO> bookmarkedRecipes = userRecipeService.getBookmarkedRecipes(user.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", bookmarkedRecipes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("북마크 레시피 조회 실패", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "북마크한 레시피를 불러올 수 없습니다.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 레시피 북마크 추가
     */
    @PostMapping("/bookmarked-recipes/{rcpSno}")
    public ResponseEntity<?> addBookmark(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        try {
            userRecipeService.addBookmark(user.getUsername(), rcpSno);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "북마크 추가 완료");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("북마크 추가 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "북마크 추가 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 레시피 북마크 취소
     */
    @DeleteMapping("/bookmarked-recipes/{rcpSno}")
    public ResponseEntity<?> removeBookmark(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        try {
            userRecipeService.removeBookmark(user.getUsername(), rcpSno);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "북마크 취소 완료");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("북마크 취소 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "북마크 취소 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 레시피 북마크 여부 확인
     */
    @GetMapping("/bookmarked-recipes/{rcpSno}/status")
    public ResponseEntity<?> checkBookmarkStatus(
            @PathVariable Long rcpSno,
            @AuthenticationPrincipal CustomerDetails user
    ) {
        boolean isBookmarked = userRecipeService.isBookmarked(user.getUsername(), rcpSno);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        
        return ResponseEntity.ok(response);
    }
}