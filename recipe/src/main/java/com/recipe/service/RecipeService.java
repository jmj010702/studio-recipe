package com.recipe.service;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.ResponseLikeStatus;
import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.exceptions.recipe.RecipeExceptions;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserReferencesService referenceService;
    private final UserService userService;

    @Transactional
    public Page<RecipeResponseDTO> readRecipePage(Pageable pageable) {
        log.info("Service readRecipePage");
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);

        log.info("Request Page page >>>>> {}", recipePage.getNumber());
        log.info("Request Page size >>>>> {}", recipePage.getSize());
        log.info("Response Page Total Elements >>>>> {}", recipePage.getTotalElements());

        return recipePage.map(RecipeResponseDTO::fromEntity);
    }

    @Transactional
    public RecipeResponseDTO findOneRecipe(Long recipeId, Long userId) {
        log.info("Service findOneRecipe - recipeId: {}, userId: {}", recipeId, userId);

        Recipe findRecipe = findByRecipeId(recipeId);

        // ✅ userId가 있을 때만 조회 기록 저장 (비로그인 사용자도 조회 가능하도록)
        if (userId != null) {
            referenceService.userRecipeView(findRecipe, userId);
        } else {
            log.info("비로그인 사용자 조회 - 조회 기록 저장 생략");
        }

        return RecipeResponseDTO.fromEntity(findRecipe);
    }

    public Recipe findByRecipeId(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(
                () -> RecipeExceptions.NOT_FOUND.getRecipeException()
        );
    }

    // ✅ 추천 레시피 가져오기 (최신순)
    public List<RecipeResponseDTO> getRecommendedRecipes(int limit) {
        log.info("Service getRecommendedRecipes - limit: {}", limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by("firstRegDt").descending());
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        
        log.info("추천 레시피 조회 결과: {}개", recipePage.getContent().size());
        
        return recipePage.getContent().stream()
                .map(RecipeResponseDTO::fromEntity)
                .toList();
    }

    // ✅ 인기 레시피 가져오기 (조회수 높은 순)
    public List<RecipeResponseDTO> getTopRecipes(int limit) {
        log.info("Service getTopRecipes - limit: {}", limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by("inqCnt").descending());
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        
        log.info("인기 레시피 조회 결과: {}개", recipePage.getContent().size());
        
        return recipePage.getContent().stream()
                .map(RecipeResponseDTO::fromEntity)
                .toList();
    }

    // ✅ 전체 레시피 조회 (페이징)
    public Page<RecipeResponseDTO> getAllRecipes(Pageable pageable) {
        log.info("Service getAllRecipes - page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        
        log.info("전체 레시피 조회 결과: {}개", recipePage.getTotalElements());
        
        return recipePage.map(RecipeResponseDTO::fromEntity);
    }

    // ✅ 레시피 검색 (제목, 요리명, 재료로 검색)
    public Page<RecipeResponseDTO> searchRecipes(String keyword, Pageable pageable) {
        log.info("Service searchRecipes - keyword: {}, page: {}, size: {}", 
                 keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Recipe> recipePage = recipeRepository.searchByKeyword(keyword, pageable);
        
        log.info("검색 결과: {}개", recipePage.getContent().size());
        
        return recipePage.map(RecipeResponseDTO::fromEntity);
    }
}