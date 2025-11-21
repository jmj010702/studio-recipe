package com.recipe.repository;

import com.recipe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // 로그인 아이디(String id) 중복 체크
    boolean existsById(String id);

    // 로그인 아이디로 회원 찾기
    Optional<User> findById(String id);
    
    // 이메일로 회원 찾기
    Optional<User> findByEmail(String email);
}