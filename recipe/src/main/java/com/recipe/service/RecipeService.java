package com.recipe.service;

// ❌ 이 4개 import 삭제
// import com.opencsv.CSVParser;
// import com.opencsv.CSVParserBuilder;
// import com.opencsv.CSVReader;
// import com.opencsv.CSVReaderBuilder;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.RecipeCreateDTO;
import com.recipe.domain.entity.Recipe;
import com.recipe.exceptions.recipe.RecipeExceptions;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;  // ❌ 이것도 삭제
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;  // ❌ 삭제
import java.io.Reader;  // ❌ 삭제
import java.nio.charset.StandardCharsets;  // ❌ 삭제
import java.time.LocalDateTime;
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
    public Long createRecipe(RecipeCreateDTO dto, Long userId) {
        log.info("Service createRecipe - userId: {}", userId);

        StringBuilder ingredientsBuilder = new StringBuilder();
        if (dto.getIngredients() != null) {
            for (RecipeCreateDTO.IngredientDTO ing : dto.getIngredients()) {
                ingredientsBuilder.append("[").append(ing.getName()).append("]");
                if (ing.getAmount() != null && !ing.getAmount().isEmpty()) {
                    ingredientsBuilder.append(" ").append(ing.getAmount());
                }
                if (ing.getUnit() != null && !ing.getUnit().isEmpty()) {
                    ingredientsBuilder.append(ing.getUnit());
                }
                if (ing.getNote() != null && !ing.getNote().isEmpty()) {
                    ingredientsBuilder.append("(").append(ing.getNote()).append(")");
                }
                ingredientsBuilder.append(" | ");
            }
        }

        Recipe recipe = Recipe.builder()
                .rcpTtl(dto.getTitle())
                .ckgNm(dto.getTitle())
                .ckgMthActoNm(dto.getDescription())
                .ckgMtrlCn(ingredientsBuilder.toString())
                .rcpImgUrl(dto.getRcpImgUrl() != null ? dto.getRcpImgUrl() : "")
                .userId(userId)
                .firstRegDt(LocalDateTime.now())
                .inqCnt(0)
                .rcmmCnt(0)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);
        
        log.info("레시피 저장 완료 - rcpSno: {}, userId: {}", savedRecipe.getRcpSno(), userId);
        
        return savedRecipe.getRcpSno();
    }

    @Transactional
    public Page<RecipeResponseDTO> readRecipePage(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(RecipeResponseDTO::fromEntity);
    }

    @Transactional
    public RecipeResponseDTO findOneRecipe(Long recipeId, Long userId) {
        Recipe findRecipe = findByRecipeId(recipeId);

        if (userId != null) {
            referenceService.userRecipeView(findRecipe, userId);
        }
        return RecipeResponseDTO.fromEntity(findRecipe);
    }

    public Recipe findByRecipeId(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(
                () -> RecipeExceptions.NOT_FOUND.getRecipeException()
        );
    }

    public List<RecipeResponseDTO> getRecommendedRecipes(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("firstRegDt").descending());
        return recipeRepository.findAll(pageable).getContent().stream()
                .map(RecipeResponseDTO::fromEntity).toList();
    }

    public List<RecipeResponseDTO> getTopRecipes(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("inqCnt").descending());
        return recipeRepository.findAll(pageable).getContent().stream()
                .map(RecipeResponseDTO::fromEntity).toList();
    }

    public Page<RecipeResponseDTO> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(RecipeResponseDTO::fromEntity);
    }

    public Page<RecipeResponseDTO> searchRecipes(String keyword, Pageable pageable) {
        return recipeRepository.searchByKeyword(keyword, pageable).map(RecipeResponseDTO::fromEntity);
    }

    // ❌ importRecipesFromCsv() 메서드 전체 삭제
}