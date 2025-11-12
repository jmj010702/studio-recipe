package com.recipe.service;

import com.recipe.domain.entity.User;
import com.recipe.exceptions.user.UserExceptions;
import com.recipe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원 단건 조회
    public User findByUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);
    }

    //이메일로 아이디 찾기
    public String findUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                UserExceptions.NOT_FOUND::getUserException);
        return user.getId();
    }

    //비밀번호 초기화
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);

        String encodePassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodePassword);
    }
    // 이메일 존재 여부 확인
    public void isUserExistsByEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);
    }

    //회원 탈퇴 (하드 딜리트)
    @Transactional
    public void deleteUser(String id, String rawPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);

        // 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 하드 딜리트
        userRepository.delete(user);
    }

}
