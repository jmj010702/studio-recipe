package com.recipe.algorithm;

import com.recipe.domain.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 개별 추천 결과를 표현하는 클래스.
 * 각 레시피에 대해 계산된 점수를 함께 저장한다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RecommendationResult {

    /**
     * 추천된 레시피 객체
     */
    private Recipe recipe;

    /**
     * 해당 레시피의 추천 점수 (유저 로그 기반 계산)
     */
    private double score;
}
