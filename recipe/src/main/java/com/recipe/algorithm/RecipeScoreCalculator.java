package com.recipe.algorithm;

import com.recipe.domain.entity.UserRecipeLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecipeScoreCalculator {

    /**
     * 사용자의 로그 데이터를 기반으로 레시피 점수를 계산.
     * 단순히 예시로서,
     * - LIKE : +3점
     * - VIEW : +1점
     * - FAVORITE : +5점
     */
    public double calculateScore(List<UserRecipeLog> logsForRecipe) {
        if (logsForRecipe == null || logsForRecipe.isEmpty()) return 0;

        double score = 0.0;
        for (UserRecipeLog log : logsForRecipe) {
            switch (log.getActionType()) {
                case LIKE -> score += 3;
                case VIEW -> score += 1;
                case FAVORITE -> score += 5;
            }
        }

        // 점수가 높은 레시피일수록 상단에 오게끔 반환
        return score;
    }
}
