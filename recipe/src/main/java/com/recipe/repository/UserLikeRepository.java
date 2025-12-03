package com.recipe.repository;

import com.recipe.domain.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    
    // 사용자의 모든 좋아요 조회
    List<UserLike> findByUserId(String userId);
    
    // 사용자가 특정 레시피를 좋아요 했는지 확인
    Optional<UserLike> findByUserIdAndRcpSno(String userId, Long rcpSno);
    
    // 사용자가 특정 레시피를 좋아요 했는지 확인 (boolean)
    boolean existsByUserIdAndRcpSno(String userId, Long rcpSno);
    
    // 사용자의 좋아요 개수
    long countByUserId(String userId);
    
    // 특정 레시피의 좋아요 개수
    long countByRcpSno(Long rcpSno);
    
    // 사용자가 좋아요한 레시피 번호 목록
    @Query("SELECT ul.rcpSno FROM UserLike ul WHERE ul.userId = :userId")
    List<Long> findRcpSnosByUserId(@Param("userId") String userId);
}