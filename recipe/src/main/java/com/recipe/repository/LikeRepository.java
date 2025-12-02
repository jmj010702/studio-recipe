package com.recipe.repository;

import com.recipe.domain.entity.Like;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    List<Like> findByUser(User user);
    
    Page<Like> findAllByUser(User user, Pageable pageable);
    
    Optional<Like> findByUserAndRecipe(User user, Recipe recipe);
    
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId")
    List<Like> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.recipe.rcpSno = :rcpSno")
    Optional<Like> findByUserIdAndRcpSno(@Param("userId") String userId, @Param("rcpSno") Long rcpSno);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user.id = :userId AND l.recipe.rcpSno = :rcpSno")
    boolean existsByUserIdAndRcpSno(@Param("userId") String userId, @Param("rcpSno") Long rcpSno);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId")
    long countByUserId(@Param("userId") String userId);
    
    void deleteByUserAndRecipe(User user, Recipe recipe);

    int countByRecipe(Recipe recipe);
}