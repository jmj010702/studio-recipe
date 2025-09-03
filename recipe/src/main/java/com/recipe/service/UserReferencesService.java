package com.recipe.service;

import com.recipe.domain.entity.User;
import com.recipe.exceptions.user.UserExceptions;
import com.recipe.repository.UserReferencesRepository;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReferencesService {

    private final UserReferencesRepository referenceRepository;
    private final UserRepository userRepository;

    public void userRecipeView(Long recipeId, Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> UserExceptions.NOT_FOUND.getUserException()
        );

    }
}
