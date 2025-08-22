package com.recipe.service;

import com.recipe.domain.entity.ApplicationRunTest;
import com.recipe.repository.ApplicationRunTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class ApplicationRunService {

    private final ApplicationRunTestRepository repository;

    @Transactional
    public ApplicationRunTest testRegister(ApplicationRunTest data) {
        return repository.save(data);
    }

    public ApplicationRunTest findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("테스트 용도 에러")
        );
    }
}
