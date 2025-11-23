package com.recipe.controller;

import com.recipe.controller.inter.LikeController;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.ResponseLikeStatus;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.entity.Recipe;
import com.recipe.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
public class LikeControllerImpl implements LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 토글 (추가/취소)
     */
    @PostMapping("/likes/{recipeId}")
    public ResponseEntity<ResponseLikeStatus> likeToRecipe(
            @PathVariable("recipeId") Long recipeId,
            @AuthenticationPrincipal CustomerDetails customer) {
        
        Long userId = customer.getUserId();
        
        log.info("Controller 좋아요 이벤트!");
        log.info("customer: {}", customer);
        log.info("userId:{}", userId);
        
        // toggleLike 호출
        LikeService.LikeResult result = likeService.toggleLike(String.valueOf(userId), recipeId);
        
        // ResponseLikeStatus 생성
        ResponseLikeStatus likeStatus = ResponseLikeStatus.builder()
                .liked(result.isLiked())
                .likeCount(result.getLikeCount())
                .build();

        return ResponseEntity.ok().body(likeStatus);
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/likes/{recipeId}")
    public ResponseEntity<Void> deleteLike(
            @PathVariable("recipeId") Long recipeId,
            @AuthenticationPrincipal CustomerDetails customer) {
        
        Long userId = customer.getUserId();
        
        likeService.removeLike(String.valueOf(userId), recipeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 좋아요 기록 반환
     */
    @GetMapping("/likes")
    public ResponseEntity<Page<RecipeResponseDTO>> likesHistory(
            @AuthenticationPrincipal CustomerDetails customer) {
        
        Long userId = customer.getUserId();
        
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        
        // getLikedRecipes 호출
        Page<Recipe> recipePage = likeService.getLikedRecipes(String.valueOf(userId), pageable);
        
        // RecipeResponseDTO 변환
        Page<RecipeResponseDTO> dtoPage = recipePage.map(RecipeResponseDTO::fromEntity);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * 추천 레시피 조회 (알고리즘 기반)
     */
    @GetMapping("/likes/recommended")
    public ResponseEntity<List<RecipeResponseDTO>> getRecommendedRecipes(
            @AuthenticationPrincipal CustomerDetails customer) {
        
        Long userId = customer.getUserId();
        
        List<Recipe> recommendedRecipes = likeService.getRecommendedRecipes(userId);
        List<RecipeResponseDTO> dtoList = recommendedRecipes.stream()
                .map(RecipeResponseDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtoList);
    }
}