package com.recipe.repository;

import com.recipe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {  // ✅ Long으로 수정 (PK 타입)

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // 로그인 아이디(String id) 중복 체크
    boolean existsById(String id);  // ✅ 이건 커스텀 메서드라 그대로 유지

    // 로그인 아이디로 회원 찾기 (커스텀 메서드)
    Optional<User> findById(String id);  // ✅ 오버로딩 - String 타입
    
    // 이메일로 회원 찾기
    Optional<User> findByEmail(String email);
    
    // PK(userId)로 회원 찾기
    Optional<User> findByUserId(Long userId);
}