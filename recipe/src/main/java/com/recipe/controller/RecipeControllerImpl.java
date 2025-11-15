package com.recipe.controller;

import com.recipe.controller.inter.RecipeController;
import com.recipe.domain.dto.PageRequestDTO;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.SortBy;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.service.RecipeService;
import com.recipe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api")
public class RecipeControllerImpl implements RecipeController {

    private final RecipeService recipeService;
    private final AuthService authService;

    // /api/recommend-recipes
    @GetMapping("/recommend-recipes")
    public ResponseEntity<Void> recommendRecipes() {
        return ResponseEntity.ok().build();
    }
    
    // /api/recipes/{recipeId}
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> detailsRecipe(@PathVariable("recipeId") Long recipeId,
                                                         @AuthenticationPrincipal CustomerDetails customer) {
        Long userId = customer.getUserId();
        log.info("UserId: {}", userId);
        RecipeResponseDTO recipe = recipeService.findOneRecipe(recipeId, userId);

        return ResponseEntity.ok(recipe);
    }

    @Operation(
            summary = "사용자 레시피 사용",
            description = "사용된 레시피를 사용 기록에 저장합니다."
    )
    // /api/details
    @PostMapping("/details")
    public ResponseEntity<Void> recipeCompletion(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }

    // ========== 추가된 API ==========
    
    @Override
    @GetMapping("/recipes/recommended")
    public ResponseEntity<?> getRecommendedRecipes() {
        try {
            log.info("GET /api/recipes/recommended - 추천 레시피 전체 조회");
            List<RecipeResponseDTO> recipes = recipeService.getRecommendedRecipes(100);
            
            return ResponseEntity.ok(Map.of(
                "data", recipes,
                "total", recipes.size()
            ));
        } catch (Exception e) {
            log.error("추천 레시피 조회 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "추천 레시피를 불러올 수 없습니다."));
        }
    }
    
    @Override
    @GetMapping("/recipes/popular")
    public ResponseEntity<?> getPopularRecipes() {
        try {
            log.info("GET /api/recipes/popular - 인기 레시피 전체 조회");
            List<RecipeResponseDTO> recipes = recipeService.getTopRecipes(100);
            
            return ResponseEntity.ok(Map.of(
                "data", recipes,
                "total", recipes.size()
            ));
        } catch (Exception e) {
            log.error("인기 레시피 조회 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "인기 레시피를 불러올 수 없습니다."));
        }
    }
    
    @Override
    @GetMapping("/recipes/all")
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            log.info("GET /api/recipes/all - 전체 레시피 조회 (page: {}, size: {})", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<RecipeResponseDTO> recipePage = recipeService.getAllRecipes(pageable);
            
            return ResponseEntity.ok(Map.of(
                "data", recipePage.getContent(),
                "total", recipePage.getTotalElements(),
                "page", recipePage.getNumber(),
                "totalPages", recipePage.getTotalPages()
            ));
        } catch (Exception e) {
            log.error("전체 레시피 조회 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "레시피를 불러올 수 없습니다."));
        }
    }
}