package com.recipe.service;

import com.recipe.domain.dto.mypage.MyPageResponseDto;
import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
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

    /**
     * 마이페이지 데이터 조회
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
        
        // 3. 내가 작성한 레시피 목록 조회 (추후 구현)
        List<Recipe> authoredRecipes = new ArrayList<>();
        
        log.info("마이페이지 데이터 조회 완료 - userId: {}", user.getUserId());
        
        return MyPageResponseDto.of(user, likedRecipes, authoredRecipes);
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
}