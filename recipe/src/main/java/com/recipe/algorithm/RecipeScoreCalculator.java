package com.recipe.algorithm;

import com.recipe.domain.entity.UserRecipeLogEntity;

import java.util.List;

/**
 * 사용자 로그 기반으로 레시피 점수를 계산하는 클래스
 */
public class RecipeScoreCalculator {

    /**
     * 특정 레시피 로그 리스트를 기반으로 점수를 계산 (double 반환)
     */
    public double calculateScore(List<UserRecipeLogEntity> logs) {
        double totalScore = 0.0;

        for (UserRecipeLogEntity log : logs) {
            double score = 0.0;

            // 행동별 가중치
            if (log.isLiked()) score += 3.0;
            if (log.isBookmarked()) score += 2.0;
            score += log.getViewCount() * 0.5;

            totalScore += score;
        }

        return totalScore;
    }
}
