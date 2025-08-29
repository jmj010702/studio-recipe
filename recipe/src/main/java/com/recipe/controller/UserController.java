package com.recipe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("studio-recipe")
public class UserController {

    @Operation(summary = "로그인", description = "아이디, 비밀번호 일치 시 토큰 발행",
    responses = {
            @ApiResponse(responseCode = "404", description = "아이디 및 비밀번호를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입", description = "회원 정보 입력")
    @GetMapping("/register")
    public ResponseEntity<Void> register() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 상세 페이지",
            description = "회원 자신의 정보를 볼 수 있다.(사용자 정보, 찜한 목록, 사용자 목록)")
    @GetMapping("/my-pages/{userId}")
    public ResponseEntity<Void> myPage(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 상세 페이지 좋아요 기록 삭제",
    description = "회원이 좋아요 눌렀던 기록 삭제(좋아요 기록, 레시피 좋아요 카운트--")
    @DeleteMapping("/my-pages")
    public ResponseEntity<Void> deleteLikeInMyPage(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 상세 페이지 수정",
    description = "회원이 수정한 데이터로 회원 테이블 수정")
    @PutMapping("/my-pages")
    public ResponseEntity<Void> updateMyPage(/*@RequestBody*/) {
        return ResponseEntity.ok().build();
    }
}
