package com.recipe.service;

import com.recipe.domain.dto.RecipeDTO;
import com.recipe.domain.entity.Recipe;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional
    public Page<RecipeDTO> readRecipePage(Pageable pageable) {
        log.info("Service readRecipePage");
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(RecipeDTO::fromEntity);
    }
}
