package com.recipe.controller;

import com.recipe.controller.inter.AuthController;
import com.recipe.controller.inter.UserController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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

    private final UserService userService;
//    private final TokenService tokenService;

    @GetMapping("/my-pages/{userId}")
    public ResponseEntity<Void> myPage(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "회원 상세 페이지 수정", description = "회원이 수정한 데이터로 회원 테이블 수정")
    @PutMapping("/my-pages")
    public ResponseEntity<Void> updateMyPage() {
        return ResponseEntity.ok().build();
    }

    //회원 탈퇴 (하드 딜리트)
//    @Operation(summary = "회원 탈퇴", description = "JWT 토큰 검증 후 자신의 계정을 완전히 삭제합니다.")
//    @DeleteMapping("/delete")
//    public ResponseEntity<Map<String, String>> deleteUser(
//            @RequestHeader("Authorization") String token,
//            @RequestBody UserDeleteRequestDto request
//    ) {
//        Map<String, String> response = new HashMap<>();
//
//        try {
//            // 1️⃣ Bearer 토큰에서 실제 JWT 부분 추출
//            String jwt = token.replace("Bearer ", "");
//
//            // 2️⃣ 토큰에서 사용자 ID 추출
//            String tokenUserId = tokenService.getUserIdFromToken(jwt);
//
//            // 3️⃣ 토큰의 사용자와 요청 ID 일치 여부 확인
//            if (!tokenUserId.equals(request.getId())) {
//                response.put("code", "403");
//                response.put("message", "본인 계정만 삭제할 수 있습니다.");
//                return ResponseEntity.status(403).body(response);
//            }
//
//            // 4️⃣ 서비스 호출
//            userService.deleteUser(request.getId(), request.getPwd());
//            response.put("code", "200");
//            response.put("message", "회원 탈퇴가 완료되었습니다.");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("회원 탈퇴 중 오류 발생", e);
//            response.put("code", "400");
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
}
