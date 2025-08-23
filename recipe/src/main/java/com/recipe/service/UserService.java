package com.recipe.service;

import com.recipe.domain.dto.UserDto;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void save(UserDto UserDto){

    }
}
