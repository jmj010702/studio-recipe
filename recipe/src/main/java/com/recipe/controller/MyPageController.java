package com.recipe.controller;

// [필수] API 명세에 맞는 DTO와 Service를 import 해야 합니다.
import com.recipe.domain.dto.mypage.MyPageResponseDto; 
import com.recipe.service.MyPageService; 

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // 👈 토큰에서 유저 정보 가져오기
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypages") // 👈 [핵심] /api/mypages 경로
public class MyPageController {

    // 💡 [필수] 이 Service가 실제로 DB를 조회해야 합니다.
    private final MyPageService myPageService;

    @GetMapping("/me") // 👈 [핵심] /me 경로 -> /api/mypages/me
    public ResponseEntity<MyPageResponseDto> getMyPageData(Authentication authentication) {
        
        // 1. Spring Security가 토큰을 해석해서 넣어준 'authentication'에서 사용자 ID를 꺼냅니다.
        String userId = authentication.getName(); // (예: "namgyu2001")

        // 2. Service에게 사용자 ID를 전달하여 모든 데이터를 가져오게 합니다.
        MyPageResponseDto myPageData = myPageService.getMyPageData(userId);

        // 3. React에게 모든 데이터를 응답합니다.
        return ResponseEntity.ok(myPageData);
    }
}