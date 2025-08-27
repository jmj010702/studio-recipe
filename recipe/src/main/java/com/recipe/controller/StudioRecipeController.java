package com.recipe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name ="레시피에 대한 컨트롤러")
@RestController
@RequestMapping("/studio-recipe")
@RequiredArgsConstructor
@Log4j2
public class StudioRecipeController {

    @GetMapping("/swaggerTest")
    @Operation(summary = "API 명세서 테스트", description = "Test",
    responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public String swaggerTest() {
        return "Test입니다.";
    }

    @GetMapping("/mainPages")
    @Operation(summary = "메인 페이지",
                         description = "전체 레시피와 추천 레시피 조회")
    public void mainPage(){

    }
}
