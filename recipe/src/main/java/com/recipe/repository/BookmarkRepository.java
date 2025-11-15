package com.recipe.repository;

import com.recipe.domain.entity.Bookmark;
import com.recipe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    /**
     * User 엔티티로 북마크 조회
     */
    List<Bookmark> findByUser(User user);
    
    /**
     * User ID로 북마크 조회
     */
    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId")
    List<Bookmark> findByUserId(@Param("userId") String userId);
    
    /**
     * User ID와 Recipe ID로 북마크 조회
     * Bookmark 엔티티의 관계: user (User), recipe (Recipe)
     * Recipe 엔티티의 ID 필드: rcpSno
     */
    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.recipe.rcpSno = :rcpSno")
    Optional<Bookmark> findByUserIdAndRcpSno(@Param("userId") String userId, @Param("rcpSno") Long rcpSno);
    
    /**
     * 북마크 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bookmark b WHERE b.user.id = :userId AND b.recipe.rcpSno = :rcpSno")
    boolean existsByUserIdAndRcpSno(@Param("userId") String userId, @Param("rcpSno") Long rcpSno);
    
    /**
     * User ID와 Recipe ID로 북마크 삭제
     * @Query와 @Modifying 사용
     */
    @Query("DELETE FROM Bookmark b WHERE b.user.id = :userId AND b.recipe.rcpSno = :rcpSno")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByUserIdAndRecipeRcpSno(@Param("userId") String userId, @Param("rcpSno") Long rcpSno);
}