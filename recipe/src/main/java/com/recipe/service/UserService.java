package com.recipe.service;

import com.recipe.domain.entity.User;
import com.recipe.exceptions.user.UserExceptions;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User findByUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> UserExceptions.NOT_FOUND.getUserException());
    }
}
