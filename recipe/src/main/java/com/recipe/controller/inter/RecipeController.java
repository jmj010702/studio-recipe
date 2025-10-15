package com.recipe.controller.inter;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.ResponseLikeStatus;
import com.recipe.domain.dto.autho.CustomerDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "레시피", description = "레시피에 대한 API 명세서")
public interface RecipeController {

    @Operation(summary = "레시피 상세 페이지", description = "레시피 상세 페이지 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "레시피 Not Found")
            })
    public ResponseEntity<RecipeResponseDTO> detailsRecipe(Long recipeId, CustomerDetails customer);

    @Operation(summary = "메인 페이지",
            description = "전체 레시피 조건에 따라 페이지 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 Page or Size 전달로 조회된 데이터 없음")
            })
    public ResponseEntity<Page<RecipeResponseDTO>> mainPage(int page, int size, String direction, String sortBy);


    @Operation(summary = "레시피 좋아요",
            description = "레시피 좋아요 정보와 사용자 좋아요 기록을 업데이트.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 이벤트 발생"),
                    @ApiResponse(responseCode = "409", description = "좋아요 중복 발생"),
                    @ApiResponse(responseCode = "500", description = "그 외 데이터베이스 제약 조건 위반")
            })
    public ResponseEntity<ResponseLikeStatus>
    likeToRecipe(Long recipeId, CustomerDetails customer);
}
