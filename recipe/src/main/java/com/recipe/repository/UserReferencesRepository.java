package com.recipe.repository;

import com.recipe.domain.entity.UserReferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserReferencesRepository extends JpaRepository<UserReferences, Long> {
    public Optional<UserReferences>  findByUser_UserIdAndRecipe_RcpSno(Long userId, Long rcpSno);
}
