package com.recipe.repository;

import com.recipe.domain.entity.UserRecipeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRecipeLogRepository extends JpaRepository<UserRecipeLog, Long> {
    List<UserRecipeLog> findByUserId(Long userId);
}
