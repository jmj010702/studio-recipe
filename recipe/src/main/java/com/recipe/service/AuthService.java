package com.recipe.service;

import com.recipe.config.JwtTokenProvider;
import com.recipe.domain.dto.autho.CustomerDetails;
import com.recipe.domain.dto.autho.TokenResponseDTO;
import com.recipe.domain.dto.user.UserLoginRequestDTO;
import com.recipe.domain.dto.user.UserRegisterRequestDTO;
import com.recipe.domain.entity.User;
import com.recipe.domain.entity.enums.Role;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(UserRegisterRequestDTO request) {
        //아이디, 이메일, 닉네임 중복을 프론트에서 검증하고 또 해야 하는지
        String password = encoder.encode(request.getPassword());

        User user = User.builder()
                .id(request.getId())
                .pwd(password)
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .birth(request.getBirth()) // 생년월일
                .gender(request.getGender())
                .role(Role.GUEST) // 기본 역할은 GUEST
                .build();

        userRepository.save(user);
        log.info("회원 가입 완료: {}", user.getId());
    }

    public TokenResponseDTO login(UserLoginRequestDTO request) {
        //아이디, 비밀번호 기반 인증 토큰
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword());

        //인증 수행, CustomerDetailService가 사용자 정보 로드, (비밀번호 비교)
        //authenticate 실행될 때CustomerDetailService - loadUserByUsername 메서드 호출
//        Authentication authentication =
//                authenticationManager.getObject().authenticate(authenticationToken);
     Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //인증 정보 저장 (요청 처리 동안 사용 가능)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        //Refresh Token은 DB or Redis에 저장하여 관리? 코드 필요

//        CustomerDetails userDetails = (CustomerDetails) authentication.getPrincipal();

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenValiditySeconds())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenValiditySeconds())
                .build();
    }

    public boolean checkExistsNickname(String nickname){
        return !userRepository.existsByNickname(nickname);
    }

    //Refresh Token 재발급

}
