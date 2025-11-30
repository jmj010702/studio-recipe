package com.recipe.service;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
import com.recipe.repository.UserReferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class UserReferencesService {

    private final UserReferencesRepository userReferencesRepository;

    @Transactional
    public void userRecipeView(Recipe recipe, Long userId) {
        if (userId == null) {
            log.info("비로그인 사용자 - 조회 기록 저장 생략");
            return;
        }

        try {
            // ✅ 이미 VIEW 기록이 있는지 확인 (중복 방지)
            Optional<UserReferences> existing = userReferencesRepository
                    .findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(
                            userId, recipe.getRcpSno(), PreferenceType.VIEW);
            
            if (existing.isPresent()) {
                log.info("이미 조회 기록이 존재함 - userId: {}, recipeId: {}", userId, recipe.getRcpSno());
                return;
            }

            // 새로운 VIEW 기록 생성
            User user = User.builder().userId(userId).build();
            
            UserReferences reference = UserReferences.builder()
                    .user(user)
                    .recipe(recipe)
                    .preference(PreferenceType.VIEW)
                    .build();
            
            userReferencesRepository.save(reference);
            log.info("조회 기록 저장 완료 - userId: {}, recipeId: {}", userId, recipe.getRcpSno());
            
        } catch (Exception e) {
            log.error("조회 기록 저장 실패 - userId: {}, recipeId: {}", userId, recipe.getRcpSno(), e);
        }
    }

    @Transactional
    public void userLikeToRecipe(Recipe recipe, User user) {
        try {
            // ✅ 중복 체크 추가
            Optional<UserReferences> existing = userReferencesRepository
                    .findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(
                            user.getUserId(), recipe.getRcpSno(), PreferenceType.LIKE);
            
            if (existing.isPresent()) {
                log.info("이미 좋아요 기록이 존재함 - userId: {}, recipeId: {}", 
                        user.getUserId(), recipe.getRcpSno());
                return;
            }
            
            UserReferences reference = UserReferences.builder()
                    .user(user)
                    .recipe(recipe)
                    .preference(PreferenceType.LIKE)
                    .build();
            
            userReferencesRepository.save(reference);
            log.info("좋아요 기록 저장 완료 - userId: {}, recipeId: {}", 
                    user.getUserId(), recipe.getRcpSno());
                    
        } catch (Exception e) {
            log.error("좋아요 기록 저장 실패 - userId: {}, recipeId: {}", 
                    user.getUserId(), recipe.getRcpSno(), e);
            throw e; // 좋아요는 실패하면 예외 던지기
        }
    }

    @Transactional
    public void deleteByReference(Recipe recipe, User user) {
        // ✅ LIKE 타입만 삭제하도록 수정
        try {
            Optional<UserReferences> likeReference = userReferencesRepository
                    .findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(
                            user.getUserId(), recipe.getRcpSno(), PreferenceType.LIKE);
            
            if (likeReference.isPresent()) {
                userReferencesRepository.delete(likeReference.get());
                log.info("좋아요 기록 삭제 완료 - userId: {}, recipeId: {}", 
                        user.getUserId(), recipe.getRcpSno());
            } else {
                log.warn("삭제할 좋아요 기록이 없음 - userId: {}, recipeId: {}", 
                        user.getUserId(), recipe.getRcpSno());
            }
            
        } catch (Exception e) {
            log.error("좋아요 기록 삭제 실패 - userId: {}, recipeId: {}", 
                    user.getUserId(), recipe.getRcpSno(), e);
            throw e;
        }
    }

    /**
     * ✅ 레시피 삭제 시 해당 레시피의 모든 사용자 참조 기록 삭제
     * @param recipeId 삭제할 레시피 ID
     */
    @Transactional
    public void deleteByRecipeId(Long recipeId) {
        try {
            int deletedCount = userReferencesRepository.deleteByRecipe_RcpSno(recipeId);
            log.info("사용자 참조 기록 삭제 완료 - recipeId: {}, count: {}", recipeId, deletedCount);
        } catch (Exception e) {
            log.error("사용자 참조 기록 삭제 실패 - recipeId: {}", recipeId, e);
            throw new RuntimeException("사용자 참조 기록 삭제 중 오류가 발생했습니다.", e);
        }
    }
}