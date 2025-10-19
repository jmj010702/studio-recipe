package com.recipe.repository;

import com.recipe.domain.entity.UserRecipeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRecipeLogRepository extends JpaRepository<UserRecipeLogEntity, Long> {
    List<UserRecipeLogEntity> findByUserId(Long userId);
}
