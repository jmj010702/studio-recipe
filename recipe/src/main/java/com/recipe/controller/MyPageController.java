package com.recipe.controller;

import com.recipe.domain.dto.IngredientDto;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.dto.mypage.MyPageResponseDto;
import com.recipe.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypages")
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 메인 정보 조회 (유저 정보 + 좋아요 목록)
    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("마이페이지 조회 요청 - userId: {}", customer.getUserId());
        MyPageResponseDto myPageInfo = myPageService.getMyPageInfo(customer.getUserId());
        return ResponseEntity.ok(myPageInfo);
    }

    // 내가 작성한 레시피 목록 조회
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getMyRecipes(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("내 레시피 목록 조회 요청 - userId: {}", customer.getUserId());
        List<RecipeResponseDTO> myRecipes = myPageService.getMyRecipes(customer.getUserId());
        return ResponseEntity.ok(myRecipes);
    }

    // ✅ 좋아요 누른 레시피 목록 조회
    @GetMapping("/liked-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getLikedRecipes(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("좋아요 레시피 목록 조회 요청 - userId: {}", customer.getUserId());
        List<RecipeResponseDTO> likedRecipes = myPageService.getLikedRecipesDTO(customer.getUserId());
        return ResponseEntity.ok(likedRecipes);
    }

    // ✅ 찜한 레시피 목록 조회 (북마크)
    @GetMapping("/bookmarked-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getBookmarkedRecipes(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("찜한 레시피 목록 조회 요청 - userId: {}", customer.getUserId());
        List<RecipeResponseDTO> bookmarkedRecipes = myPageService.getBookmarkedRecipes(customer.getUserId());
        return ResponseEntity.ok(bookmarkedRecipes);
    }

    // ========================================
    // ✅ 냉장고 재료 관련 엔드포인트 (신규 추가)
    // ========================================

    /**
     * 냉장고 재료 조회
     */
    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientDto>> getIngredients(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("냉장고 재료 조회 - userId: {}", customer.getUserId());
        List<IngredientDto> ingredients = myPageService.getIngredients(customer.getUserId());
        return ResponseEntity.ok(ingredients);
    }

    /**
     * 냉장고 재료 추가
     */
    @PostMapping("/ingredients")
    public ResponseEntity<IngredientDto> addIngredient(
            @AuthenticationPrincipal CustomerDetails customer,
            @RequestBody IngredientDto ingredientDto) {
        log.info("냉장고 재료 추가 - userId: {}, ingredient: {}", customer.getUserId(), ingredientDto.getName());
        IngredientDto saved = myPageService.addIngredient(customer.getUserId(), ingredientDto);
        return ResponseEntity.ok(saved);
    }

    /**
     * 냉장고 재료 삭제
     */
    @DeleteMapping("/ingredients/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @AuthenticationPrincipal CustomerDetails customer,
            @PathVariable Long ingredientId) {
        log.info("냉장고 재료 삭제 - userId: {}, ingredientId: {}", customer.getUserId(), ingredientId);
        myPageService.deleteIngredient(customer.getUserId(), ingredientId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 재료 기반 추천 레시피 조회
     */
    @GetMapping("/recommended-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getRecommendedRecipes(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("재료 기반 추천 레시피 조회 - userId: {}", customer.getUserId());
        List<RecipeResponseDTO> recipes = myPageService.getRecommendedRecipes(customer.getUserId());
        return ResponseEntity.ok(recipes);
    }
}