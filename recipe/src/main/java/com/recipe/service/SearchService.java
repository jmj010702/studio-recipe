package com.recipe.service;

import com.recipe.algorithm.IngredientRecommendAlgorithm;
import com.recipe.algorithm.RecipeRecommendAlgorithm;
import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.entity.Recipe;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final RecipeRepository recipeRepository;
    private final IngredientRecommendAlgorithm ingredientRecommendAlgorithm;
    private final RecipeRecommendAlgorithm recipeRecommendAlgorithm;

    /**
     * 레시피명으로 검색
     */
    public Page<RecipeResponseDTO> searchByTitle(String title, Pageable pageable) {
        log.info("레시피명 검색 - title: {}", title);
        
        if (title == null || title.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        
        // ✅ searchByTitle → searchByKeyword로 변경
        Page<Recipe> recipes = recipeRepository.searchByKeyword(title.trim(), pageable);
        return recipes.map(RecipeResponseDTO::fromEntity);
    }

    /**
     * 재료로 검색 (알고리즘 사용)
     * 입력: "돼지고기,양파,마늘" 또는 List<String>
     */
    public Page<RecipeResponseDTO> searchByIngredients(String ingredientsStr, Pageable pageable) {
        log.info("재료 검색 - ingredients: {}", ingredientsStr);
        
        // 콤마로 구분된 재료 파싱
        List<String> ingredients = Arrays.stream(ingredientsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        return searchByIngredientsList(ingredients, pageable);
    }

    /**
     * 재료 리스트로 검색 (알고리즘 사용 + 필터링)
     * ✅ 모든 재료를 포함하는 레시피만 반환
     */
    public Page<RecipeResponseDTO> searchByIngredientsList(List<String> ingredients, Pageable pageable) {
        log.info("재료 검색 (List) - ingredients: {}", ingredients);
        
        if (ingredients == null || ingredients.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // ✅ 알고리즘 추천 결과 가져오기
        List<IngredientRecommendAlgorithm.IngredientRecommendationResult> results = 
                ingredientRecommendAlgorithm.recommendByIngredients(ingredients);
        
        log.info("알고리즘 추천 결과: {}개", results.size());
        
        // ✅ 모든 재료를 포함하는 레시피만 필터링
        List<IngredientRecommendAlgorithm.IngredientRecommendationResult> filteredResults = results.stream()
                .filter(result -> {
                    Recipe recipe = recipeRepository.findById(result.getRecipeId()).orElse(null);
                    
                    // 레시피가 없거나 재료 정보가 없으면 제외
                    if (recipe == null || recipe.getCkgMtrlCn() == null) {
                        return false;
                    }
                    
                    String recipeIngredients = recipe.getCkgMtrlCn(); // 레시피의 재료 문자열
                    
                    // 모든 검색 재료가 레시피에 포함되어 있는지 확인
                    return ingredients.stream()
                            .allMatch(searchIngredient -> 
                                recipeIngredients.contains(searchIngredient)
                            );
                })
                .collect(Collectors.toList());
        
        log.info("필터링 전: {}개 → 필터링 후: {}개", results.size(), filteredResults.size());
        
        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredResults.size());
        
        if (start >= filteredResults.size()) {
            return Page.empty(pageable);
        }
        
        // Recipe 엔티티 조회 후 DTO 변환
        List<RecipeResponseDTO> dtoList = filteredResults.subList(start, end).stream()
                .map(result -> {
                    Recipe recipe = recipeRepository.findById(result.getRecipeId())
                            .orElse(null);
                    return recipe != null ? RecipeResponseDTO.fromEntity(recipe) : null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, filteredResults.size());
    }

    /**
     * 개인화 추천 레시피 (로그인 사용자용)
     */
    public Page<RecipeResponseDTO> getPersonalizedRecommendations(Long userId, Pageable pageable) {
        log.info("개인화 추천 - userId: {}", userId);
        
        // ✅ 기존 알고리즘 사용
        List<RecommendationResult> results = recipeRecommendAlgorithm.recommendRecipes(userId);
        
        log.info("개인화 추천 결과: {}개", results.size());
        
        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());
        
        if (start >= results.size()) {
            return Page.empty(pageable);
        }
        
        List<RecipeResponseDTO> dtoList = results.subList(start, end).stream()
                .map(result -> RecipeResponseDTO.fromEntity(result.getRecipe()))
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, results.size());
    }

    /**
     * 재료 자동완성
     */
    public List<String> getIngredientSuggestions(String keyword) {
        log.info("재료 자동완성 - keyword: {}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        // 일반적인 재료 목록 (실제로는 DB나 캐시에서 가져와야 함)
        List<String> commonIngredients = Arrays.asList(
                // 육류
                "돼지고기", "돼지목살", "돼지등심", "삼겹살", "소고기", "소등심", "소갈비", 
                "닭고기", "닭가슴살", "닭다리", "오리고기",
                // 채소
                "양파", "대파", "쪽파", "마늘", "생강", "고추", "청양고추", "홍고추",
                "당근", "감자", "고구마", "무", "배추", "양배추", "상추", "깻잎",
                "버섯", "표고버섯", "느타리버섯", "팽이버섯", "새송이버섯",
                "파프리카", "호박", "애호박", "가지", "오이", "토마토",
                // 곡물/콩
                "두부", "순두부", "콩나물", "숙주", "쌀", "찹쌀", "밀가루", "부침가루",
                // 해산물
                "멸치", "새우", "오징어", "주꾸미", "낙지", "조개", "바지락", "홍합",
                "고등어", "삼치", "갈치", "명태", "대구",
                // 계란/유제품
                "계란", "달걀", "우유", "치즈", "버터", "생크림",
                // 양념
                "간장", "된장", "고추장", "쌈장", "고춧가루", "참기름", "들기름",
                "식용유", "올리브유", "소금", "설탕", "후추", "식초", "물엿", "맛술",
                "다진마늘", "생강가루", "카레가루", "깨", "통깨", "참깨"
        );
        
        String searchKeyword = keyword.trim().toLowerCase();
        
        return commonIngredients.stream()
                .filter(ing -> ing.toLowerCase().contains(searchKeyword))
                .limit(10)
                .collect(Collectors.toList());
    }
}