package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * 레시피명 검색
     * GET /api/search/title?q=김치찌개&page=0&size=15
     */
    @GetMapping("/title")
    public ResponseEntity<Page<RecipeResponseDTO>> searchByTitle(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {  // 🔥 15개로 변경
        
        log.info("레시피명 검색 요청 - query: {}, page: {}, size: {}", query, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDTO> results = searchService.searchByTitle(query, pageable);
        
        return ResponseEntity.ok(results);
    }

    /**
     * 재료 검색 (알고리즘 사용)
     * GET /api/search/ingredients?q=돼지고기,양파,마늘&page=0&size=15
     */
    @GetMapping("/ingredients")
    public ResponseEntity<Page<RecipeResponseDTO>> searchByIngredients(
            @RequestParam("q") String ingredients,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {  // 🔥 15개로 변경
        
        log.info("재료 검색 요청 - ingredients: {}, page: {}, size: {}", ingredients, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDTO> results = searchService.searchByIngredients(ingredients, pageable);
        
        return ResponseEntity.ok(results);
    }

    /**
     * 재료 자동완성
     * GET /api/search/ingredients/suggestions?q=돼
     */
    @GetMapping("/ingredients/suggestions")
    public ResponseEntity<List<String>> getIngredientSuggestions(
            @RequestParam("q") String keyword) {
        
        log.info("재료 자동완성 요청 - keyword: {}", keyword);
        
        List<String> suggestions = searchService.getIngredientSuggestions(keyword);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 개인화 추천 (로그인 사용자용)
     * GET /api/search/recommendations?page=0&size=15
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Page<RecipeResponseDTO>> getRecommendations(
            @AuthenticationPrincipal CustomerDetails customer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {  // 🔥 15개로 변경
        
        if (customer == null) {
            log.info("비로그인 사용자 - 인기 레시피 반환");
            // 비로그인 시 빈 페이지 또는 인기 레시피 반환
            return ResponseEntity.ok(Page.empty());
        }
        
        Long userId = customer.getUserId();
        log.info("개인화 추천 요청 - userId: {}, page: {}, size: {}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponseDTO> results = searchService.getPersonalizedRecommendations(userId, pageable);
        
        return ResponseEntity.ok(results);
    }
}