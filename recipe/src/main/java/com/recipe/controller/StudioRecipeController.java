package com.recipe.controller;

import com.recipe.domain.entity.Recipe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name ="레시피", description = "레시피에 대한 API 명세서")
@RestController
@RequestMapping("/studio-recipe")
@RequiredArgsConstructor
@Log4j2
public class StudioRecipeController {

    @GetMapping("/main-pages")
    @Operation(summary = "메인 페이지",
                         description = "전체 레시피와 추천 레시피 조회",
                         responses = {
                                @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ResponseEntity<Void> mainPage(){

        return ResponseEntity.ok().build();
    }

    @Operation(summary="FindConditionPage", description = "조건에 따라 레시피를 정렬 반환합니다.")
    @GetMapping("/main-pages/{condition}")
    public ResponseEntity<Void> findPageCondition(
            @Parameter(name="조회수, 좋아요 순, 최신순", example="조회수") //날짜 순, 역순?
            @PathVariable("condition") String condition) {

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "레시피 상세 페이지", description = "레시피 상세 페이지 반환",
    responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "레시피 Not Found")
    })
    @GetMapping("/details/{recipeId}")
    public ResponseEntity<Void> detailsRecipe(@PathVariable("recipeId") Long recipeId){
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "레시피 좋아요",
            description = "레시피 좋아요 정보와 사용자 좋아요 기록을 업데이트.")
    @PatchMapping("/details")
    public ResponseEntity<Void> likeToRecipe(/*@RequestBody*/){
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
