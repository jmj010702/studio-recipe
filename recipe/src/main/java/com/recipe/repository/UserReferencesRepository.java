package com.recipe.repository;

import com.recipe.domain.entity.UserReferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReferencesRepository extends JpaRepository<UserReferences, Long> {

}
