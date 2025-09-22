package com.recipe.controller;

import com.recipe.domain.dto.autho.NicknameAvailabilityResponse;
import com.recipe.domain.dto.autho.TokenResponseDTO;
import com.recipe.domain.dto.user.UserLoginRequestDTO;
import com.recipe.domain.dto.user.UserRegisterRequestDTO;
import com.recipe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("studio-recipe/auth")
@Tag(name = "Auth", description = "인증 및 사용자 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "아이디, 비밀번호 일치 시 토큰 발행",
    responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, 토근 발행"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 맞지 않습니다."),
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO request) {
        TokenResponseDTO tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "회원가입", description = "사용자 계정 생성",
    responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는...")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDTO request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "닉네임 중복 확인",
            description = "닉네임이 중복되는지 확인하여 Boolean 타입과 메시지를 전달",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameAvailabilityResponse> checkNickname(
            @RequestParam String nickname){

        boolean isAvailable = authService.checkExistsNickname(nickname);
        String message = isAvailable ? "사용 가능한 닉네임입니다." : "현재 사용중인 닉네임입니다.";
        NicknameAvailabilityResponse response = NicknameAvailabilityResponse.builder()
                .isAvailable(isAvailable)
                .message(message)
                .build();
        return  ResponseEntity.ok(response);
    }
}
