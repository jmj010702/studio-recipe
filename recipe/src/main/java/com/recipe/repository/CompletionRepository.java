package com.recipe.repository;

import com.recipe.domain.entity.RecipeCompletions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletionRepository extends JpaRepository<RecipeCompletions, Long> {
    
    @Modifying
    @Query("DELETE FROM RecipeCompletions c WHERE c.recipe.rcpSno = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}