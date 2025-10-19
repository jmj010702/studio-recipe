package com.recipe.controller;

import com.recipe.controller.inter.RecipeController;
import com.recipe.domain.dto.PageRequestDTO;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.ResponseLikeStatus;
import com.recipe.domain.dto.SortBy;
import com.recipe.domain.dto.autho.CustomerDetails;
import com.recipe.service.RecipeService;
import com.recipe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class RecipeControllerImpl implements RecipeController {

    private final RecipeService recipeService;
    private final AuthService authService;

    @GetMapping("/recommend-recipes")
    public ResponseEntity<Void> recommendRecipes() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<RecipeResponseDTO> detailsRecipe(@PathVariable("recipeId") Long recipeId,
                                              @AuthenticationPrincipal CustomerDetails customer) {
        Long userId = customer.getUserId();
        log.info("UserId: {}", userId);
        RecipeResponseDTO recipe = recipeService.findOneRecipe(recipeId, userId);

        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/main-pages")
    public ResponseEntity<Page<RecipeResponseDTO>> mainPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "CREATED_AT") String sortBy
    ){
        PageRequestDTO requestPage = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .direction(direction)
                .sortBy(SortBy.formString(sortBy))
                .build();

        Pageable pageable = requestPage.getPageable();
        Page<RecipeResponseDTO> recipePage = recipeService.readRecipePage(pageable);

        return ResponseEntity.ok(recipePage);
    }

    @Operation(
            summary = "사용자 레시피 사용",
            description = "사용된 레시피를 사용 기록에 저장합니다."
    )
    @PostMapping("/details")
    public ResponseEntity<Void> recipeCompletion(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }

    //사용자 기록 추가
    //사용자 사용 기록 반환 C
    //사용자 사용 기록 삭제

    //냉장고 재료 등록

}
