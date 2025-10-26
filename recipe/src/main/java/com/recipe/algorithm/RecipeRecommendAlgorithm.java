package com.recipe.algorithm;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.UserRecipeLogEntity;
import com.recipe.repository.UserRecipeLogRepository;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecipeRecommendAlgorithm {

    private  final UserRecipeLogRepository userRecipeLogRepository;
    private  final RecipeRepository recipeRepository;
    private  RecipeScoreCalculator scoreCalculator;

    /**
     * 주어진 사용자 ID에 대한 추천 레시피 계산
     */
    public List<RecommendationResult> recommendRecipes(Long userId) {
        // 사용자 로그 가져오기
        List<UserRecipeLogEntity> userLogs = userRecipeLogRepository.findByUser_UserId(userId);

        if (userLogs.isEmpty()) {
            // 만약 로그가 없다면 전체 인기 레시피 TOP 10 반환 (기본 추천)
            return recipeRepository.findTop10ByOrderByRcmmCntDesc().stream()
                    .map(recipe -> new RecommendationResult(recipe, 0.0))
                    .collect(Collectors.toList());
        }

        //레시피별 로그 그룹화
        Map<Recipe, List<UserRecipeLogEntity>> groupedLogs = userLogs.stream()
                .collect(Collectors.groupingBy(UserRecipeLogEntity::getRecipe));

        //각 레시피의 점수 계산
        List<RecommendationResult> results = groupedLogs.entrySet().stream()
                .map(entry -> new RecommendationResult(
                        entry.getKey(),
                        scoreCalculator.calculateScore(entry.getValue())
                ))
                .collect(Collectors.toList());

        // 점수가 높은 순으로 정렬하여 상위 10개 반환
        return results.stream()
                .sorted(Comparator.comparingDouble(RecommendationResult::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
