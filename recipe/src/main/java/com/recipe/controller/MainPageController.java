package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;  // ✅ 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mainPages")
@RequiredArgsConstructor
@Log4j2  // ✅ 추가
public class MainPageController {
    
    private final RecipeService recipeService;
    
    @GetMapping
    public ResponseEntity<?> getMainPage() {
        log.info("GET /api/mainPages - 메인 페이지 데이터 요청");
        
        try {
            List<RecipeResponseDTO> recommendedRecipes = recipeService.getRecommendedRecipes(10);
            List<RecipeResponseDTO> topRecipes = recipeService.getTopRecipes(10);
            
            Map<String, Object> data = new HashMap<>();
            data.put("recommended-recipe", recommendedRecipes);
            data.put("recipe", topRecipes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            
            log.info("메인 페이지 데이터 반환 완료 - 추천: {}개, 인기: {}개", 
                    recommendedRecipes.size(), topRecipes.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("메인 페이지 데이터 조회 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "레시피를 불러오는데 실패했습니다."));
        }
    }
}