package com.recipe.service;

import com.recipe.algorithm.RecipeRecommendAlgorithm;
import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecipeRecommendService {

    private final RecipeRecommendAlgorithm recommendAlgorithm;

    // 반환 타입을 List<RecipeResponseDTO>로 변경
    public List<RecipeResponseDTO> getRecommendedRecipes(Long userId) {
        
        log.info("Service: 추천 알고리즘 실행 요청 - userId: {}", userId);

        List<RecommendationResult> results = recommendAlgorithm.recommendRecipes(userId);
        
        log.info("Service: 알고리즘 결과 {}개 반환됨", results.size());

        // RecommendationResult를 RecipeResponseDTO로 변환
        return results.stream()
                .map(result -> RecipeResponseDTO.fromEntity(result.getRecipe()))
                .collect(Collectors.toList());
    }
}