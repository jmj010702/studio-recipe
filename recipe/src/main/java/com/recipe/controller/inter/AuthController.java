package com.recipe.controller.inter;

import com.recipe.domain.dto.auth.NicknameAvailabilityResponse;
import com.recipe.domain.dto.auth.TokenResponseDTO;
import com.recipe.domain.dto.user.UserLoginRequestDTO;
import com.recipe.domain.dto.user.UserRegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable; // 👈 [추가]
import org.springframework.web.bind.annotation.RequestParam; // 👈 [추가]

@Tag(name = "Auth", description = "인증 및 사용자 관련 API")
public interface AuthController {

    @Operation(summary = "로그인", description = "아이디, 비밀번호 일치 시 토큰 발행",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공, 토근 발행"),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 맞지 않습니다."),
            })
    ResponseEntity<TokenResponseDTO> login(UserLoginRequestDTO request);

    
    // ▼▼▼▼▼ [수정 1] React가 /signup을 호출하므로 메서드 이름을 변경 ▼▼▼▼▼
    @Operation(summary = "회원가입", description = "사용자 계정 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는...")
            })
    ResponseEntity<Void> signup(UserRegisterRequestDTO request); // 👈 register -> signup
    // ▲▲▲▲▲ [수정 1] ▲▲▲▲▲


    @Operation(summary = "아이디/닉네임 중복 확인",
            description = "타입(id, nickname)과 값(value)을 받아 중복 여부 전달",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능"),
                    @ApiResponse(responseCode = "409", description = "이미 사용 중 (중복)")
            })
    ResponseEntity<NicknameAvailabilityResponse> checkDuplication(@PathVariable String type, 
                                                                  @RequestParam String value);

}