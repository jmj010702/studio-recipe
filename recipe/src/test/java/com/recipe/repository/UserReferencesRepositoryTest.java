package com.recipe.repository;

import com.recipe.domain.entity.Recipe;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.UserReferences;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 내장 DB(H2) 사용을 강제
//스프링 관련 빈까지 주입하지 않고 JPA 관련 빈만 주입하고 내장 DB를 사용하여 빠른 테스트를 하려 했지만
//RECIPE 데이터가 많은 경우로 디스크 메모리 사용, 통합 테스트
@SpringBootTest
@Log4j2
class UserReferencesRepositoryTest {

    private UserReferencesRepository userReferencesRepository;
    private UserRepository userRepository;
    private RecipeRepository recipeRepository;
    private EntityManager entityManager;

    @Autowired
    public UserReferencesRepositoryTest(UserReferencesRepository userReferencesRepository,
                                        UserRepository userRepository, RecipeRepository recipeRepository,
                                        EntityManager entityManager) {
        this.userReferencesRepository = userReferencesRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void save() {
        //given
        User user = userRepository.findById(1L).get();
        Recipe recipe = recipeRepository.findById(1L).get();

        UserReferences userReferences = UserReferences.builder()
                .user(user)
                .recipe(recipe)
                .build();

        //when
        UserReferences save = userReferencesRepository.save(userReferences);
        log.info("UserReferences Data >>>>>>>>> {}", save);
        UserReferences result = userReferencesRepository.findById(1L).get();
        log.info("UserReferences Data >>>>>>>>> {}", result);

        //then
        entityManager.flush();
        entityManager.clear();
        Assertions.assertThat(result).isEqualTo(save);
    }


    /*@BeforeEach
    public void init() {
        for(int i = 0; i < 10; i++) {
            User user = User.builder()
                    .id("a" + i)
                    .pwd("pwd" + i)
                    .name("b" + i)
                    .nickname("n" + i)
                    .email("e" + i)
                    .age(i)
                    .role(Role.GUEST)
                    .build();
            userRepository.save(user);
        }
    }*/
}