package com.recipe.controller;

import com.recipe.domain.dto.Recipe.RecipeResponseDTO;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.dto.mypage.MyPageResponseDto;
import com.recipe.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypages")
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 메인 정보 조회 (유저 정보 + 좋아요 목록)
    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("마이페이지 조회 요청 - userId: {}", customer.getUserId());
        MyPageResponseDto myPageInfo = myPageService.getMyPageInfo(customer.getUserId());
        return ResponseEntity.ok(myPageInfo);
    }

    // 내가 작성한 레시피 목록 조회
    @GetMapping("/my-recipes")
    public ResponseEntity<List<RecipeResponseDTO>> getMyRecipes(@AuthenticationPrincipal CustomerDetails customer) {
        log.info("내 레시피 목록 조회 요청 - userId: {}", customer.getUserId());
        List<RecipeResponseDTO> myRecipes = myPageService.getMyRecipes(customer.getUserId());
        return ResponseEntity.ok(myRecipes);
    }
}