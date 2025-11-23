package com.recipe.controller;

import com.recipe.controller.inter.RecipeController;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.dto.RecipeCreateDTO;
import com.recipe.service.AuthService;
import com.recipe.service.RecipeService;
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

    // ✅ 레시피 등록 API
    @Override
    @PostMapping("/recipes/write")
    public ResponseEntity<?> writeRecipe(
            @RequestBody RecipeCreateDTO request,
            @AuthenticationPrincipal CustomerDetails customer) {
        
        try {
            if (customer == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "로그인이 필요합니다."));
            }

            log.info("레시피 작성 요청 - 사용자: {}, 제목: {}", 
                    customer.getUsername(), request.getTitle());
            
            Long recipeId = recipeService.createRecipe(request, customer.getUserId());
            
            return ResponseEntity.ok(Map.of(
                "message", "레시피가 성공적으로 등록되었습니다.",
                "recipeId", recipeId
            ));
            
        } catch (Exception e) {
            log.error("레시피 작성 실패", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "레시피 작성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/recommend-recipes")
    public ResponseEntity<Void> recommendRecipes() {
        return ResponseEntity.ok().build();
    }
    
    @Override
    @GetMapping("/recipes/recommended")
    public ResponseEntity<?> getRecommendedRecipes() {
        List<RecipeResponseDTO> recipes = recipeService.getRecommendedRecipes(100);
        return ResponseEntity.ok(Map.of("data", recipes, "total", recipes.size()));
    }
    
    @Override
    @GetMapping("/recipes/popular")
    public ResponseEntity<?> getPopularRecipes() {
        List<RecipeResponseDTO> recipes = recipeService.getTopRecipes(100);
        return ResponseEntity.ok(Map.of("data", recipes, "total", recipes.size()));
    }
    
    @Override
    @GetMapping("/recipes/all")
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDTO> recipePage = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(Map.of(
            "data", recipePage.getContent(),
            "total", recipePage.getTotalElements(),
            "page", recipePage.getNumber(),
            "totalPages", recipePage.getTotalPages()
        ));
    }
    
    @GetMapping("/recipes/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "5") Integer limit
    ) {
        int actualSize = (limit != null && limit > 0) ? limit : size;
        Pageable pageable = PageRequest.of(page, actualSize);
        Page<RecipeResponseDTO> recipePage = recipeService.searchRecipes(keyword, pageable);
        
        return ResponseEntity.ok(Map.of(
            "data", recipePage.getContent(),
            "keyword", keyword,
            "total", recipePage.getTotalElements(),
            "page", recipePage.getNumber(),
            "totalPages", recipePage.getTotalPages()
        ));
    }
    
    @Override
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> detailsRecipe(
            @PathVariable("recipeId") Long recipeId,
            @AuthenticationPrincipal CustomerDetails customer) {
        
        Long userId = (customer != null) ? customer.getUserId() : null;
        RecipeResponseDTO recipe = recipeService.findOneRecipe(recipeId, userId);
        
        return ResponseEntity.ok(recipe);
    }

    @Operation(summary = "사용자 레시피 사용", description = "사용된 레시피를 사용 기록에 저장합니다.")
    @PostMapping("/details")
    public ResponseEntity<Void> recipeCompletion() {
        return ResponseEntity.ok().build();
    }
}