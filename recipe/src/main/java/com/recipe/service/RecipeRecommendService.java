package com.recipe.service;

import com.recipe.algorithm.RecipeRecommendAlgorithm;
import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 추천 로직을 실제 컨트롤러에서 호출할 수 있도록 연결하는 서비스 계층
 */
@Service
@RequiredArgsConstructor
public class RecipeRecommendService {

    private final RecipeRecommendAlgorithm recommendAlgorithm;

    /**
     * 컨트롤러에서 호출하는 추천 메서드
     */
    public List<Recipe> getRecommendedRecipes(Long userId) {
        List<RecommendationResult> results = recommendAlgorithm.recommendRecipes(userId);
        return results.stream()
                .map(RecommendationResult::getRecipe)
                .collect(Collectors.toList());
    }
}
