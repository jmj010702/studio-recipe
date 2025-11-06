package com.recipe.controller;

import com.recipe.algorithm.IngredientRecommendAlgorithm;
import com.recipe.domain.dto.IngredientRecommendationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend/ingredient")
@RequiredArgsConstructor
public class IngredientRecommendController {

    private final IngredientRecommendAlgorithm ingredientRecommendAlgorithm;

    /**
     * 사용자의 재료 리스트 기반으로 추천 레시피 반환
     * 예: POST /api/recommend/ingredient
     *     body: ["양파", "달걀", "소금"]
     */
    @PostMapping
    public IngredientRecommendationResponseDto recommend(@RequestBody List<String> ingredients) {
        var results = ingredientRecommendAlgorithm.recommendByIngredients(ingredients);
        return IngredientRecommendationResponseDto.of(results);
    }
}
