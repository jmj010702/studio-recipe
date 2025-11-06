// src/page/LoginPage.jsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
// import api from '../api/axios'; // (Mocking)
import './login.css'; 

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); 
    
    try {
      // 1. (Mock) localStorage에서 'users' 목록을 가져옴
      const existingUsers = JSON.parse(localStorage.getItem('fake_users') || '[]');

      // ▼▼▼▼▼ (핵심 수정) ▼▼▼▼▼
      // 2. (Mock) 입력한 값의 앞뒤 공백을 제거합니다.
      const inputUsername = username.trim();
      const inputPassword = password.trim();
      // ▲▲▲▲▲ (핵심 수정) ▲▲▲▲▲

      // 3. (Mock) 공백 제거된 아이디로 사용자 찾기
      const foundUser = existingUsers.find(user => 
        // 3-1. (수정) 저장된 값도 공백 제거 후 비교
        user.username.trim() === inputUsername
      );

      // 4. (Mock) 사용자 검증 (아이디 없음)
      if (!foundUser) {
        alert('아이디 또는 비밀번호가 일치하지 않습니다.');
        return;
      }

      // 5. (Mock) 사용자 검증 (비밀번호 틀림)
      if (foundUser.password.trim() !== inputPassword) { // 5-1. (수정) 저장된 값도 공백 제거
        alert('아이디 또는 비밀번호가 일치하지 않습니다.');
        return;
      }

      // 6. (Mock) 로그인 성공!
      console.log(' (Mock) 로그인 성공:', foundUser);
      sessionStorage.setItem('logged_in_user', JSON.stringify(foundUser));
      
      // 7. 메인 페이지로 이동
      navigate('/'); 

    } catch (error) {
      console.error('Mock 로그인 실패:', error);
      alert('임시 로그인 중 로컬 스토리지 오류가 발생했습니다.');
    }
  };

  return (
    // ... (JSX 렌더링 부분은 변경 없습니다) ...
    <div className="login-page-container">
      <div className="login-box">
        <h2>로그인</h2>
        <form onSubmit={handleLogin}>
          <div className="input-group">
            <label htmlFor="username">아이디</label>
            <input 
              type="text" 
              id="username" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required 
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input 
              type="password" 
              id="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required 
            />
          </div>
          <button type="submit" className="login-btn">
            로그인
          </button>
          <Link to="/signup" className="signup-btn-link">
            <button type="button" className="signup-btn">
              회원가입
            </button>
          </Link>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;