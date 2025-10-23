package com.recipe.controller;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.dto.ApiResponse;
import com.recipe.service.RecipeRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecipeRecommendController {

    private final RecipeRecommendService recommendService;

    /**
     * 특정 사용자에게 추천 레시피 목록을 반환
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Recipe>>> getRecommendations(@PathVariable Long userId) {
        // 기존 서비스 호출 그대로 유지
        List<Recipe> recommendations = recommendService.getRecommendedRecipes(userId);

        log.info("[추천 요청] userId={} → 추천된 레시피 수={}", userId, recommendations.size());

        // 응답 구조만 개선
        ApiResponse<List<Recipe>> response = ApiResponse.success(
                recommendations,
                recommendations.isEmpty()
                        ? "추천 가능한 레시피가 없습니다."
                        : "추천 레시피 조회 성공"
        );

        return ResponseEntity.ok(response);
    }
}
