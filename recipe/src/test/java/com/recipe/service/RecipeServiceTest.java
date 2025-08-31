package com.recipe.service;

import com.recipe.domain.dto.PageRequestDTO;
import com.recipe.domain.entity.Recipe;
import com.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class RecipeServiceTest {

    private final RecipeRepository repository;

    @Autowired
   public RecipeServiceTest(RecipeRepository repository) {
        this.repository = repository;
    }

    @Test
    @DisplayName("PageRequestDTO Exception Check")
    public void pagingRecipe() throws IOException {
        StringBuilder sb = new StringBuilder();
        PageRequestDTO request = PageRequestDTO.builder()
                .page(0)
                .size(10).build();
        log.info("Request -> Page Count >>>>>>>> {}", request.getPage());
        log.info("Request >>>>>>>> {}", request);
        Page<Recipe> page = repository.findAll(request.getPageable());
        page.stream().forEach(i -> sb.append(i + "\n"));
        log.info("PAGE DATA >>>>>>>>>>>>>> {}", sb.toString());
    }
}