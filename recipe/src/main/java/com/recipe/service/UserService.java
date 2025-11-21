package com.recipe.service;

import com.recipe.domain.entity.User;
import com.recipe.domain.dto.user.ChangePasswordRequestDTO; // DTO가 있다면 import
import com.recipe.exceptions.user.UserExceptions;
import com.recipe.repository.LikeRepository;
import com.recipe.repository.UserRepository;
import com.recipe.repository.UserReferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;             // [추가] 좋아요 삭제용
    private final UserReferencesRepository userReferencesRepository; // [추가] 조회기록/찜 삭제용
    private final PasswordEncoder passwordEncoder;

    // 회원 단건 조회 (PK인 userId로 조회)
    public User findByUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);
    }

    // 이메일로 아이디 찾기 (로그인 아이디 반환)
    public String findUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                UserExceptions.NOT_FOUND::getUserException);
        return user.getId();
    }

    // 회원 가입 시 아이디/닉네임/이메일 중복 체크용 (AuthController에서 사용)
    public boolean checkExistsId(String id) {
        return userRepository.existsById(id); // 아이디(String)로 조회하는 메서드 필요
    }

    public boolean checkExistsNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void isUserExistsByEmail(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);
    }

    // 비밀번호 찾기 후 재설정
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);

        String encodePassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodePassword);
    }

    // 마이페이지 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPwd())) {
            throw UserExceptions.INVALID_PASSWORD.getUserException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw UserExceptions.INVALID_PASSWORD.getUserException("새 비밀번호가 서로 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    // ▼▼▼ [추가됨] 회원 탈퇴 로직 ▼▼▼
    @Transactional
    public void deleteUser(String loginId, String password) {
        // 1. 로그인 아이디(String)로 사용자 조회
        // (주의: Repository에 Optional<User> findById(String id); 가 있어야 함. 
        //  없다면 findByEmail 처럼 findById를 만들어야 합니다.)
        User user = userRepository.findById(loginId) 
                .orElseThrow(UserExceptions.NOT_FOUND::getUserException);

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPwd())) {
            throw UserExceptions.INVALID_PASSWORD.getUserException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 연관 데이터 먼저 삭제 (FK 제약조건 해결)
        // (Repository에 deleteByUserId 메서드가 없다면 추가해야 합니다)
        Long userIdPk = user.getUserId();
        
        try {
            likeRepository.deleteByUserId(userIdPk);           // 좋아요 기록 삭제
            userReferencesRepository.deleteByUserId(userIdPk); // 최근 본 레시피/참조 기록 삭제
            
            // 만약 작성한 레시피도 지워야 한다면:
            // recipeRepository.deleteByUserId(userIdPk);
            
        } catch (Exception e) {
            log.error("연관 데이터 삭제 중 오류 발생: {}", e.getMessage());
            // 필요 시 예외를 던지거나 무시 (로그만 남김)
        }

        // 4. 사용자 삭제
        userRepository.delete(user);
        log.info("회원 탈퇴 완료: {}", loginId);
    }
    // ▲▲▲ [추가 끝] ▲▲▲
}