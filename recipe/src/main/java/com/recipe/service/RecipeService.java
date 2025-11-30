package com.recipe.service;

import com.recipe.domain.dto.RecipeCreateDTO;
import com.recipe.domain.dto.RecipeWriteRequestDTO;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;  // ✅ 수정: Recipe 패키지 추가
import com.recipe.domain.entity.Recipe;
import com.recipe.exceptions.recipe.RecipeExceptions;
import com.recipe.repository.RecipeRepository;
import com.recipe.repository.LikeRepository;
import com.recipe.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public Long createRecipe(RecipeCreateDTO dto, Long userId, String imageUrl) {
        log.info("Service createRecipe - userId: {}, imageUrl: {}", userId, imageUrl);

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
                .rcpImgUrl(imageUrl != null ? imageUrl : (dto.getRcpImgUrl() != null ? dto.getRcpImgUrl() : ""))
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
    public Recipe saveRecipe(RecipeWriteRequestDTO request, String imageUrl, Long userId) {
        StringBuilder ingredientsBuilder = new StringBuilder();
        if (request.getIngredients() != null) {
            for (RecipeWriteRequestDTO.Ingredient ing : request.getIngredients()) {
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
                .rcpTtl(request.getTitle())
                .ckgNm(request.getTitle())
                .ckgMthActoNm(request.getIntroduction())
                .ckgMtrlCn(ingredientsBuilder.toString())
                .rcpImgUrl(imageUrl != null ? imageUrl : "")
                .userId(userId)
                .firstRegDt(LocalDateTime.now())
                .inqCnt(0)
                .rcmmCnt(0)
                .build();
        
        return recipeRepository.save(recipe);
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
            findRecipe.increaseViewCount();
            referenceService.userRecipeView(findRecipe, userId);
        }
        return RecipeResponseDTO.fromEntity(findRecipe);
    }

    public Recipe findByRecipeId(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(
                () -> RecipeExceptions.NOT_FOUND.getRecipeException()
        );
    }

    @Transactional
    public void deleteRecipe(Long recipeId, Long userId) {
        log.info("Service deleteRecipe - recipeId: {}, userId: {}", recipeId, userId);
        
        Recipe recipe = findByRecipeId(recipeId);
        
        if (!recipe.getUserId().equals(userId)) {
            log.warn("삭제 권한 없음 - recipeId: {}, recipe.userId: {}, request.userId: {}", 
                    recipeId, recipe.getUserId(), userId);
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        try {
            int deletedLikes = likeRepository.deleteByRecipeId(recipeId);
            log.info("좋아요 삭제 완료 - recipeId: {}, count: {}", recipeId, deletedLikes);
            
            int deletedBookmarks = bookmarkRepository.deleteByRecipeId(recipeId);
            log.info("북마크 삭제 완료 - recipeId: {}, count: {}", recipeId, deletedBookmarks);
            
            referenceService.deleteByRecipeId(recipeId);
            log.info("사용자 참조 기록 삭제 완료 - recipeId: {}", recipeId);
            
            if (recipe.getRcpImgUrl() != null && !recipe.getRcpImgUrl().isEmpty()) {
                deleteImageFile(recipe.getRcpImgUrl());
            }
            
            recipeRepository.delete(recipe);
            log.info("레시피 삭제 완료 - recipeId: {}", recipeId);
            
        } catch (Exception e) {
            log.error("레시피 삭제 중 오류 발생 - recipeId: {}", recipeId, e);
            throw new RuntimeException("레시피 삭제 중 오류가 발생했습니다.", e);
        }
    }
    
    private void deleteImageFile(String imageUrl) {
        try {
            if (imageUrl.startsWith("http")) {
                log.info("외부 URL 이미지는 삭제하지 않음: {}", imageUrl);
                return;
            }
            
            String fileName = imageUrl;
            if (imageUrl.contains("/")) {
                fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            }
            
            fileName = fileName.replace("images/", "").replace("images\\", "");
            
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
            Path filePath = Paths.get(uploadDir + fileName);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("이미지 파일 삭제 성공: {}", fileName);
            } else {
                log.info("이미지 파일이 존재하지 않음: {}", fileName);
            }
            
        } catch (IOException e) {
            log.error("이미지 파일 삭제 실패: {}", imageUrl, e);
        }
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
}