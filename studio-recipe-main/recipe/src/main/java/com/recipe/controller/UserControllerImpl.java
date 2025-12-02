package com.recipe.controller;

import com.recipe.controller.inter.UserController;
import com.recipe.domain.dto.user.UserDeleteRequestDto;
import com.recipe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/my-pages/{userId}")
    public ResponseEntity<Void> myPage(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 상세 페이지 수정", description = "회원이 수정한 데이터로 회원 테이블 수정")
    @PutMapping("/my-pages")
    public ResponseEntity<Void> updateMyPage() {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "인증된 사용자의 계정을 완전히 삭제합니다. 비밀번호 확인이 필요합니다."
    )
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDeleteRequestDto request
    ) {
        String authenticatedUserId = userDetails.getUsername();
        log.info("회원 탈퇴 요청 - 사용자 ID: {}", authenticatedUserId);

        userService.deleteUser(authenticatedUserId, request.getPassword());

        return ResponseEntity.noContent().build();
    }
}