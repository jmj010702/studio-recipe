package com.recipe.controller;

import com.recipe.controller.inter.UserController;
import com.recipe.domain.dto.auth.CustomerDetails;
import com.recipe.domain.dto.user.ChangePasswordRequestDTO;
import com.recipe.domain.dto.user.UserDeleteRequestDto;
import com.recipe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.recipe.domain.dto.user.UserDeleteRequestDto;
import com.recipe.service.UserService;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    // ▼▼▼ [중요] 서비스가 연결되어 있어야 로직을 수행합니다. ▼▼▼
    private final UserService userService; 

    @GetMapping("/my-pages/{userId}")
    public ResponseEntity<Void> myPage(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 상세 페이지 수정",
            description = "회원이 수정한 데이터로 회원 테이블 수정")

    @PutMapping("/my-pages")
    public ResponseEntity<Void> updateMyPage() {
        return ResponseEntity.ok().build();
    }

    // ▼▼▼ [추가됨] 1. 비밀번호 변경 API ▼▼▼
    @Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal CustomerDetails customer,
            @RequestBody @Valid ChangePasswordRequestDTO request) {
        
        userService.changePassword(customer.getUserId(), request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // ▼▼▼ [추가됨] 2. 회원 탈퇴 API (이게 없어서 404가 떴음) ▼▼▼
    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴를 진행합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomerDetails customer,
            @RequestBody @Valid UserDeleteRequestDto request) {
        
        log.info("회원 탈퇴 요청 - 사용자 ID(PK): {}", customer.getUserId());
        
        // CustomerDetails에서 로그인 ID(username)를 가져와서 삭제 요청
        userService.deleteUser(customer.getUsername(), request.getPassword());

        return ResponseEntity.noContent().build();
    }
}
