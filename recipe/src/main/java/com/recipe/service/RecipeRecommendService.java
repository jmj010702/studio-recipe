package com.recipe.service;

import com.recipe.algorithm.RecipeRecommendAlgorithm;
import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeRecommendService {

    private final RecipeRecommendAlgorithm algorithm;

    public List<Recipe> getRecommendedRecipes(Long userId) {
        List<RecommendationResult> results = algorithm.recommendRecipes(userId);
        return results.stream()
                .map(RecommendationResult::getRecipe)
                .collect(Collectors.toList());
    }
}
