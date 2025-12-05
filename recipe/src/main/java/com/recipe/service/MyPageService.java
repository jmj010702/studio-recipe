package com.recipe.service;

import com.recipe.domain.dto.IngredientDto;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.mypage.MyPageResponseDto;
import com.recipe.domain.entity.Bookmark;
import com.recipe.domain.entity.Ingredient;
import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
import com.recipe.repository.BookmarkRepository;
import com.recipe.repository.IngredientRepository;
import com.recipe.repository.RecipeRepository;
import com.recipe.repository.UserRepository;
import com.recipe.repository.UserReferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class MyPageService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserReferencesRepository userReferencesRepository;
    private final BookmarkRepository bookmarkRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * 마이페이지 데이터 조회 (username 기반)
     * @param username 사용자 ID (Spring Security의 username)
     * @return 마이페이지 응답 DTO
     */
    public MyPageResponseDto getMyPageData(String username) {
        
        log.info("마이페이지 데이터 조회 시작 - username(id): {}", username);
        
        // 1. 사용자 조회 (ID로)
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + username));

        log.info("사용자 조회 완료 - userId: {}, nickname: {}", user.getUserId(), user.getNickname());

        // 2. 좋아요 누른 레시피 목록 조회
        List<Recipe> likedRecipes = getLikedRecipes(user.getUserId());
        
        log.info("좋아요 레시피 조회 완료 - {}개", likedRecipes.size());
        
        // 3. 내가 작성한 레시피 목록 조회
        List<Recipe> authoredRecipes = getAuthoredRecipes(user.getUserId());
        
        log.info("작성 레시피 조회 완료 - {}개", authoredRecipes.size());
        log.info("마이페이지 데이터 조회 완료 - userId: {}", user.getUserId());
        
        return MyPageResponseDto.of(user, likedRecipes, authoredRecipes);
    }

    /**
     * 마이페이지 정보 조회 (userId 기반) - Controller에서 직접 호출
     * @param userId 사용자 고유 ID
     * @return 마이페이지 응답 DTO
     */
    public MyPageResponseDto getMyPageInfo(Long userId) {
        log.info("마이페이지 정보 조회 - userId: {}", userId);
        
        // 1. 사용자 조회 (userId로)
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId: " + userId));
        
        // 2. 좋아요 누른 레시피 목록 조회
        List<Recipe> likedRecipes = getLikedRecipes(userId);
        
        // 3. 내가 작성한 레시피 목록 조회
        List<Recipe> authoredRecipes = getAuthoredRecipes(userId);
        
        return MyPageResponseDto.of(user, likedRecipes, authoredRecipes);
    }

    /**
     * 내가 작성한 레시피 목록 조회 (userId 기반) - Controller에서 직접 호출
     * @param userId 사용자 고유 ID
     * @return 작성한 레시피 DTO 리스트
     */
    public List<RecipeResponseDTO> getMyRecipes(Long userId) {
        log.info("내 레시피 목록 조회 - userId: {}", userId);
        
        List<Recipe> myRecipes = getAuthoredRecipes(userId);
        
        return myRecipes.stream()
                .map(RecipeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 찜한 레시피 목록 조회 (북마크)
     * @param userId 사용자 고유 ID
     * @return 찜한 레시피 DTO 리스트
     */
    public List<RecipeResponseDTO> getBookmarkedRecipes(Long userId) {
        log.info("찜한 레시피 목록 조회 - userId: {}", userId);
        
        // User 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 북마크 목록 조회
        List<Bookmark> bookmarks = bookmarkRepository.findByUser(user);
        
        // Recipe 추출 및 DTO 변환
        return bookmarks.stream()
                .map(Bookmark::getRecipe)
                .map(RecipeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 좋아요 누른 레시피 목록 조회 (DTO 반환)
     * @param userId 사용자 고유 ID
     * @return 좋아요 레시피 DTO 리스트
     */
    public List<RecipeResponseDTO> getLikedRecipesDTO(Long userId) {
        log.info("좋아요 레시피 목록 조회 - userId: {}", userId);
        
        List<Recipe> likedRecipes = getLikedRecipes(userId);
        
        return likedRecipes.stream()
                .map(RecipeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 냉장고 재료 조회
     * @param userId 사용자 고유 ID
     * @return 재료 DTO 리스트
     */
    public List<IngredientDto> getIngredients(Long userId) {
        log.info("냉장고 재료 조회 - userId: {}", userId);
        
        List<Ingredient> ingredients = ingredientRepository.findByUserId(userId);
        return ingredients.stream()
                .map(this::convertToIngredientDto)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 냉장고 재료 추가
     * @param userId 사용자 고유 ID
     * @param dto 재료 정보
     * @return 저장된 재료 DTO
     */
    @Transactional
    public IngredientDto addIngredient(Long userId, IngredientDto dto) {
        log.info("냉장고 재료 추가 - userId: {}, name: {}", userId, dto.getName());
        
        Ingredient ingredient = Ingredient.builder()
                .userId(userId)
                .name(dto.getName())
                .quantity(dto.getQuantity())  // String 그대로 사용
                .unit(dto.getUnit())
                .memo(dto.getMemo())
                .build();
        
        Ingredient saved = ingredientRepository.save(ingredient);
        return convertToIngredientDto(saved);
    }

    /**
     * ✅ 냉장고 재료 삭제
     * @param userId 사용자 고유 ID
     * @param ingredientId 재료 ID
     */
    @Transactional
    public void deleteIngredient(Long userId, Long ingredientId) {
        log.info("냉장고 재료 삭제 - userId: {}, ingredientId: {}", userId, ingredientId);
        
        ingredientRepository.deleteByIdAndUserId(ingredientId, userId);
    }

    /**
     * ✅ 재료 기반 추천 레시피 조회 (교집합 방식으로 수정)
     * @param userId 사용자 고유 ID
     * @return 추천 레시피 DTO 리스트
     */
    public List<RecipeResponseDTO> getRecommendedRecipes(Long userId) {
        log.info("재료 기반 추천 레시피 조회 - userId: {}", userId);
        
        // 1. 사용자의 냉장고 재료 조회
        List<Ingredient> ingredients = ingredientRepository.findByUserId(userId);
        log.info("사용자 냉장고 재료 수: {}", ingredients.size());
        
        // 재료가 없으면 빈 리스트 반환
        if (ingredients.isEmpty()) {
            log.info("냉장고 재료가 없습니다 - userId: {}", userId);
            return new ArrayList<>();
        }
        
        // 2. 재료 이름 추출
        List<String> ingredientNames = ingredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());
        log.info("재료 목록: {}", ingredientNames);
        
        // 3. 첫 번째 재료로 초기 레시피 목록 조회
        List<Recipe> matchedRecipes = recipeRepository.findByckgMtrlCnContaining(ingredientNames.get(0));
        log.info("초기 레시피 수 ({}): {}개", ingredientNames.get(0), matchedRecipes.size());
        
        // 4. 나머지 재료들로 교집합 필터링 (AND 조건)
        for (int i = 1; i < ingredientNames.size(); i++) {
            String ingredientName = ingredientNames.get(i);
            
            // 현재 재료가 포함된 레시피만 남기기 (교집합)
            matchedRecipes = matchedRecipes.stream()
                    .filter(recipe -> {
                        String materialContent = recipe.getCkgMtrlCn();
                        return materialContent != null && materialContent.contains(ingredientName);
                    })
                    .collect(Collectors.toList());
            
            log.info("{} 재료 필터링 후: {}개", ingredientName, matchedRecipes.size());
        }
        
        log.info("최종 매칭된 레시피 (교집합): {}개", matchedRecipes.size());
        
        // 5. 조회수 기준으로 정렬
        matchedRecipes.sort((r1, r2) -> {
            int r1Cnt = r1.getInqCnt() != null ? r1.getInqCnt() : 0;
            int r2Cnt = r2.getInqCnt() != null ? r2.getInqCnt() : 0;
            return Integer.compare(r2Cnt, r1Cnt);
        });
        
        return matchedRecipes.stream()
                .map(RecipeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 좋아요 누른 레시피 목록 조회 (N+1 문제 해결)
     * @param userId 사용자 ID
     * @return 좋아요 레시피 리스트
     */
    private List<Recipe> getLikedRecipes(Long userId) {
        // fetch join을 사용하여 한 번에 레시피까지 조회
        List<UserReferences> userReferences = 
                userReferencesRepository.findAllByUserIdAndPreferenceWithRecipe(userId, PreferenceType.LIKE);

        return userReferences.stream()
                .map(UserReferences::getRecipe)
                .collect(Collectors.toList());
    }

    /**
     * 내가 작성한 레시피 목록 조회
     * @param userId 사용자 ID
     * @return 작성한 레시피 리스트
     */
    private List<Recipe> getAuthoredRecipes(Long userId) {
        List<Recipe> recipes = recipeRepository.findByUserId(userId);
        return recipes != null ? recipes : new ArrayList<>();
    }

    /**
     * ✅ Ingredient -> IngredientDto 변환 (수정됨)
     * @param ingredient 재료 엔티티
     * @return 재료 DTO
     */
    private IngredientDto convertToIngredientDto(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())  // String 타입이므로 그대로 사용
                .unit(ingredient.getUnit())
                .memo(ingredient.getMemo())
                .build();
    }
}