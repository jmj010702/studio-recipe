package com.recipe.service;

import com.recipe.algorithm.RecipeRecommendAlgorithm;
import com.recipe.algorithm.RecommendationResult;
import com.recipe.domain.entity.Like;
import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
import com.recipe.repository.LikeRepository;
import com.recipe.repository.RecipeRepository;
import com.recipe.repository.UserReferencesRepository;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserReferencesRepository userReferencesRepository;
    private final RecipeRecommendAlgorithm recommendAlgorithm;

    /**
     * 좋아요 추가 (UserReferences에 LIKE 기록)
     */
    public void addLike(String username, Long recipeId) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

        // 중복 확인
        Optional<Like> checkTheLike = likeRepository.findByUserAndRecipe(user, recipe);
        if (checkTheLike.isPresent()) {
            throw new RuntimeException("이미 좋아요한 레시피입니다.");
        }

        // Like 저장
        Like like = Like.builder()
                .user(user)
                .recipe(recipe)
                .build();
        likeRepository.save(like);

        // UserReferences에 LIKE 기록 (알고리즘용)
        Optional<UserReferences> existingRef = userReferencesRepository
                .findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(user.getUserId(), recipeId, PreferenceType.LIKE);
        
        if (existingRef.isEmpty()) {
            UserReferences userRef = UserReferences.builder()
                    .user(user)
                    .recipe(recipe)
                    .preference(PreferenceType.LIKE)
                    .build();
            userReferencesRepository.save(userRef);
        }

        log.info("좋아요 추가 및 UserReferences 기록 완료 - username: {}, recipeId: {}", username, recipeId);
    }

    /**
     * 좋아요한 레시피 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<Recipe> getLikedRecipes(String username, Pageable pageable) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Page<Like> likePage = likeRepository.findAllByUser(user, pageable);
        return likePage.map(Like::getRecipe);
    }

    /**
     * 좋아요 취소
     */
    public void removeLike(String username, Long recipeId) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

        Like like = likeRepository.findByUserAndRecipe(user, recipe)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        likeRepository.delete(like);
        
        // UserReferences에서도 LIKE 기록 삭제
        userReferencesRepository.deleteByUserAndRecipeAndPreference(user, recipe, PreferenceType.LIKE);
        
        log.info("좋아요 취소 완료 - username: {}, recipeId: {}", username, recipeId);
    }

    /**
     * 좋아요 상태 확인
     */
    @Transactional(readOnly = true)
    public boolean isLiked(String username, Long recipeId) {
        try {
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

            return likeRepository.findByUserAndRecipe(user, recipe).isPresent();
        } catch (Exception e) {
            log.error("좋아요 상태 확인 실패 - username: {}, recipeId: {}", username, recipeId, e);
            return false;
        }
    }

    /**
     * 좋아요 토글 (추가/취소) + 좋아요 수 반환
     */
    public LikeResult toggleLike(String username, Long recipeId) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

        Optional<Like> existingLike = likeRepository.findByUserAndRecipe(user, recipe);
        boolean isLiked;
        
        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            
            // UserReferences에서도 삭제
            userReferencesRepository.deleteByUserAndRecipeAndPreference(user, recipe, PreferenceType.LIKE);
            
            isLiked = false;
            log.info("좋아요 취소 - username: {}, recipeId: {}", username, recipeId);
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .recipe(recipe)
                    .build();
            likeRepository.save(like);
            
            // UserReferences에 LIKE 기록 (중복 체크)
            Optional<UserReferences> existingRef = userReferencesRepository
                    .findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(user.getUserId(), recipeId, PreferenceType.LIKE);
            
            if (existingRef.isEmpty()) {
                UserReferences userRef = UserReferences.builder()
                        .user(user)
                        .recipe(recipe)
                        .preference(PreferenceType.LIKE)
                        .build();
                userReferencesRepository.save(userRef);
            }
            
            isLiked = true;
            log.info("좋아요 추가 - username: {}, recipeId: {}", username, recipeId);
        }
        
        // 해당 레시피의 총 좋아요 수 계산 (이 레시피를 좋아요한 모든 사용자 수)
        int likeCount = likeRepository.countByRecipe(recipe);
        
        return new LikeResult(isLiked, likeCount);
    }

    /**
     * 추천 레시피 조회 (알고리즘 기반)
     */
    @Transactional(readOnly = true)
    public List<Recipe> getRecommendedRecipes(Long userId) {
        List<RecommendationResult> results = recommendAlgorithm.recommendRecipes(userId);
        return results.stream()
                .map(RecommendationResult::getRecipe)
                .collect(Collectors.toList());
    }

    /**
     * 좋아요 토글 결과 DTO
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class LikeResult {
        private boolean liked;
        private int likeCount;
    }
}