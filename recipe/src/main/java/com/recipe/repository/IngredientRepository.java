package com.recipe.repository;

import com.recipe.domain.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    List<Ingredient> findByUserId(Long userId);
    
    @Transactional
    @Modifying
    void deleteByIdAndUserId(Long id, Long userId);
}