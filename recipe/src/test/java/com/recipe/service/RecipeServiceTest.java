package com.recipe.service;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.SortBy;
import com.recipe.exceptions.recipe.RecipeException;
import com.recipe.exceptions.recipe.RecipeExceptions;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class RecipeServiceTest {

    private final RecipeService service;

    @Autowired
   public RecipeServiceTest(RecipeService service) {
        this.service = service;
    }


    @Test
    @DisplayName("Page 정상 반환 검증")
    public void readPageRecipe(){
        //given
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Order.asc(SortBy.CREATED_AT.getFieldName())));

        //when
        Page<RecipeResponseDTO> response = service.readRecipePage(pageable);
        log.info(response.getContent().toString());
        //then
        assertNotNull(response);
    }

    @Test
    @DisplayName("Page 및 Size 잘못된 개수를 입력하여 가져온 데이터가 없을 때 NotFoundException 검증")
    public void pagingRecipeNotFoundException() {
        //given
        Pageable pageable = PageRequest.of(1000000, 10,
                Sort.by(SortBy.CREATED_AT.getFieldName()).descending());

        //when
        //then
        RecipeException recipeException = assertThrows(RecipeException.class,
                () -> {
                    service.readRecipePage(pageable);
                }
        );
        org.assertj.core.api.Assertions.assertThat(recipeException.getCode())
                .isEqualTo(RecipeExceptions.NOT_FOUND.getRecipeException().getCode());
    }

    @Test
    @DisplayName("레시피 단건 조회")
    public void findOneRecipe() {
        RecipeResponseDTO findByResult = service.findOneRecipe(1L);
        log.info("FindByResult >>>>> {}", findByResult);
    }

    @Test
    @DisplayName("잘못된 RECIPE ID 조회")
    public void findOneException(){
        RecipeException recipeException = assertThrows(RecipeException.class, () -> {
            service.findOneRecipe(1000000000L);
        });

        org.assertj.core.api.Assertions.assertThat(recipeException.getCode())
                .isEqualTo(RecipeExceptions.NOT_FOUND.getRecipeException().getCode());
    }
}