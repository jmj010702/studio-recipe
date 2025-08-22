package com.recipe.service;

import com.recipe.domain.entity.ApplicationRunTest;
import com.recipe.domain.entity.Gender;
import com.recipe.domain.entity.Role;
import com.recipe.repository.ApplicationRunTestRepository;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ApplicationRunServiceTest {

    private final ApplicationRunTestRepository repository;

    @Autowired
    public ApplicationRunServiceTest(ApplicationRunTestRepository repository) {
        this.repository = repository;
    }

    @Test
    void registerAndFind() {
        ApplicationRunTest data = ApplicationRunTest.builder()
                        .name("테스트입니다.").build();
        ApplicationRunTest result = repository.save(data);
        log.info("data 저장 결과 >>> {}",result);

        ApplicationRunTest result2 = repository.findById(result.getId()).get();
        log.info("Find By Id >>> {}",result2);
        Assertions.assertThat(result).isEqualTo(result);
    }

}