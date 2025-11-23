package com.recipe.controller.inter;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.dto.RecipeCreateDTO;  // ✅ 수정
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Operation(summary = "레시피 작성", description = "새로운 레시피를 작성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "작성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    public ResponseEntity<?> writeRecipe(@RequestBody RecipeCreateDTO request, CustomerDetails customer);  // ✅ 수정
}