package com.recipe.controller;

import com.recipe.controller.inter.AuthController;
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
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO request) {
        TokenResponseDTO tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDTO request) {
        log.info("================ register ================");
        log.info("request = {}", request);

        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameAvailabilityResponse> checkNickname(
            @RequestParam String nickname){

        boolean isAvailable = authService.checkExistsNickname(nickname);
        String message = isAvailable ?  "현재 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.";
        NicknameAvailabilityResponse response = NicknameAvailabilityResponse.builder()
                .isAvailable(isAvailable)
                .message(message)
                .build();
        return  ResponseEntity.ok(response);
    }
}
