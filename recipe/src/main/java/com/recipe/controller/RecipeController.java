package com.recipe.controller;

import com.recipe.domain.dto.PageRequestDTO;
import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
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

@Tag(name = "레시피", description = "레시피에 대한 API 명세서")
@RestController
@RequestMapping("/studio-recipe")
@RequiredArgsConstructor
@Log4j2
public class RecipeController {

    private final RecipeService recipeService;
    private final AuthService authService;

    @GetMapping("/main-pages")
    @Operation(summary = "메인 페이지",
            description = "전체 레시피 조건에 따라 페이지 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 Page or Size 전달로 조회된 데이터 없음")
            })
    public ResponseEntity<?> mainPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "CREATED_AT") String sortBy) {

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

    @GetMapping("/recommend-recipes")
    public ResponseEntity<Void> recommendRecipes() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "레시피 상세 페이지", description = "레시피 상세 페이지 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "레시피 Not Found")
            })
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<Void> detailsRecipe(@PathVariable("recipeId") Long recipeId,
                                              @AuthenticationPrincipal CustomerDetails customer) {

        Long userId = customer.getUserId();
        log.info("UserId: {}", userId);
        recipeService.findOneRecipe(recipeId, userId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "레시피 좋아요",
            description = "레시피 좋아요 정보와 사용자 좋아요 기록을 업데이트.")
    @PatchMapping("/details")
    public ResponseEntity<Void> likeToRecipe(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "사용자 레시피 사용",
            description = "사용된 레시피를 사용 기록에 저장합니다."
    )
    @PostMapping("/details")
    public ResponseEntity<Void> recipeCompletion(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }

}
