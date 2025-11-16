package com.recipe.controller.inter;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "레시피", description = "레시피에 대한 API 명세서")
public interface RecipeController {

    @Operation(summary = "레시피 상세 페이지", description = "레시피 상세 페이지 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "레시피 Not Found")
            })
    public ResponseEntity<RecipeResponseDTO> detailsRecipe(Long recipeId, CustomerDetails customer);

    @Operation(summary = "추천 레시피 전체 조회", description = "추천 레시피 목록 반환 (최신순)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    public ResponseEntity<?> getRecommendedRecipes();

    @Operation(summary = "인기 레시피 전체 조회", description = "인기 레시피 목록 반환 (조회수 높은 순)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    public ResponseEntity<?> getPopularRecipes();

    @Operation(summary = "전체 레시피 조회", description = "전체 레시피 목록 반환 (페이징)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            })
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );
}