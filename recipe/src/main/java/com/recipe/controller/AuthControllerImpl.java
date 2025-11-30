package com.recipe.controller;

import com.recipe.controller.inter.AuthController;
import com.recipe.domain.dto.auth.*;
import com.recipe.domain.dto.user.UserLoginRequestDTO;
import com.recipe.domain.dto.user.UserRegisterRequestDTO;
import com.recipe.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final MailService mailService;
    private final VerificationCodeService verificationCodeService;
    private final TokenService tokenService;
    private final UserService userService;

    // (로그인 - 수정 없음)
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO request) {
        TokenResponseDTO tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    // ▼▼▼ [수정 1] React가 /auth/signup을 호출하므로 경로 수정 ▼▼▼
    @Override 
    @PostMapping("/signup") // 👈 /register에서 /signup으로 변경
    public ResponseEntity<Void> signup(@RequestBody @Valid UserRegisterRequestDTO request) {
        log.info("================ signup (was register) ================");
        log.info("request = {}", request);

        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // ▲▲▲ [수정 1] ▲▲▲


    // ▼▼▼ [수정 2] React의 /auth/check/{type}?value=... 요청에 맞게 메서드 수정 ▼▼▼
    @Override 
    @GetMapping("/check/{type}") // 👈 /check-nickname에서 /check/{type}으로 변경
    public ResponseEntity<NicknameAvailabilityResponse> checkDuplication(
            @PathVariable String type,
            @RequestParam String value) { // 👈 파라미터를 'value'로 받음

        boolean isAvailable = false;
        String message = "";

        // 1. 'id' (username) 중복 확인
        if ("id".equals(type)) {
            // (AuthService에 checkExistsId가 구현되어 있어야 함)
            isAvailable = authService.checkExistsId(value); 
            message = isAvailable ? "현재 사용중인 아이디입니다." : "사용 가능한 아이디입니다.";
        } 
        // 2. 'nickname' 중복 확인
        else if ("nickname".equals(type)) {
            isAvailable = authService.checkExistsNickname(value);
            message = isAvailable ? "현재 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        } 
        else {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        // 3. React에 409(중복) 또는 200(성공) 응답
        if (isAvailable) {
            // 409 Conflict (중복됨)
            NicknameAvailabilityResponse response = NicknameAvailabilityResponse.builder()
                    .isAvailable(true)
                    .message(message)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); 
        } else {
            // 200 OK (사용 가능)
            NicknameAvailabilityResponse response = NicknameAvailabilityResponse.builder()
                    .isAvailable(false)
                    .message(message)
                    .build();
            return ResponseEntity.ok(response);
        }
    }
    // ▲▲▲ [수정 2] ▲▲▲
    

    // --- (이하 비밀번호 찾기 등은 수정하지 않음) ---

    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequest request) {
        userService.isUserExistsByEmail(request.getEmail());
        String code = verificationCodeService.generateAndSaveCode(request.getEmail());
        mailService.sendVerificationEmail(request.getEmail(), code);
        return ResponseEntity.ok("인증 번호 성공적으로 발송되었습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResetProcessResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean isVerified = verificationCodeService
                .verifyCode(request.getEmail(), request.getVerificationCode());

        if(isVerified) {
            String resetToken = tokenService.createToken(request.getEmail(),
                    request.getPurpose());
            return ResponseEntity.ok(new ResetProcessResponse("이메일 인증이 성공했습니다.", resetToken));
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResetProcessResponse("인증 번호가 유효하지 않거나 만료되었습니다.", null));
        }
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody TokenRequest request) {
        Optional<String> emailOptional =
                tokenService.validateTokenAndGetEmail(request.getToken(),  TokenPurpose.FIND_ID);

        if(emailOptional.isPresent()) {
            String email = emailOptional.get();
            tokenService.invalidateToken(request.getToken());
            String userId = userService.findUserIdByEmail(email);
            return ResponseEntity.ok(userId);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<String> emailOptional =
                tokenService.validateTokenAndGetEmail(request.getToken(),
                        TokenPurpose.RESET_PASSWORD);

        if(emailOptional.isPresent()) {
            String email = emailOptional.get();
            tokenService.invalidateToken(request.getToken());
            userService.resetPassword(email, request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("유효하지 않거나 만료된 토큰입니다.");
        }
    }
}