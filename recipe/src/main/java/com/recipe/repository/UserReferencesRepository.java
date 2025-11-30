package com.recipe.repository;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import com.recipe.domain.entity.enums.PreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserReferencesRepository extends JpaRepository<UserReferences, Long> {
    
    /**
     * ✅ 특정 사용자의 특정 레시피에 대한 특정 타입의 첫 번째 기록 조회
     * user.userId와 recipe.rcpSno로 접근 (언더스코어 제거)
     */
    Optional<UserReferences> findFirstByUser_UserIdAndRecipe_RcpSnoAndPreference(
            Long userId, Long recipeId, PreferenceType preference);
    
    /**
     * ✅ 특정 사용자의 특정 레시피에 대한 기록 조회
     */
    Optional<UserReferences> findByUserUserIdAndRecipeRcpSno(Long userId, Long recipeId);
    
    /**
     * ✅ 특정 사용자와 레시피 조합의 기록 삭제
     */
    void deleteByUserAndRecipe(User user, Recipe recipe);
    
    /**
     * ✅ 특정 사용자의 특정 타입(좋아요, 조회 등) 레시피 목록 조회
     */
    List<UserReferences> findAllByUserUserIdAndPreference(Long userId, PreferenceType preference);
    
    /**
     * ✅ 특정 사용자의 특정 타입 레시피 목록 조회 (N+1 문제 해결 - fetch join 사용)
     * JPQL이므로 정상 작동
     */
    @Query("SELECT ur FROM UserReferences ur " +
           "JOIN FETCH ur.recipe " +
           "WHERE ur.user.userId = :userId AND ur.preference = :preference")
    List<UserReferences> findAllByUserIdAndPreferenceWithRecipe(
            @Param("userId") Long userId, 
            @Param("preference") PreferenceType preference);
    
    /**
     * ✅ 특정 사용자와 레시피, 선호도 타입으로 기록 삭제 (좋아요 취소용)
     */
    void deleteByUserAndRecipeAndPreference(User user, Recipe recipe, PreferenceType preference);

    /**
     * ✅ 회원 탈퇴 시 참조 기록 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM UserReferences ur WHERE ur.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    /**
     * ✅ 전체 UserReferences 조회 (알고리즘에서 사용)
     */
    List<UserReferences> findAll();

    /**
     * ✅ 레시피 삭제 시 해당 레시피의 모든 참조 기록 삭제
     * UserReferencesService에서 사용
     */
    @Modifying
    @Query("DELETE FROM UserReferences ur WHERE ur.recipe.rcpSno = :rcpSno")
    int deleteByRecipe_RcpSno(@Param("rcpSno") Long rcpSno);
}