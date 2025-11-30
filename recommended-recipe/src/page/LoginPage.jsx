// src/page/LoginPage.jsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api, { login } from '../api/axios'; // login 헬퍼 함수 import
import './login.css'; 

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); 
    
    // 입력값 검증
    if (!username.trim() || !password) {
      alert('아이디와 비밀번호를 입력해주세요.');
      return;
    }

    setIsLoading(true);

    const loginData = {
      id: username.trim(),
      password: password
    };

    try {
      // 백엔드에 로그인 요청
      const response = await api.post('/api/auth/login', loginData); 
      
      console.log('로그인 응답:', response.data);
      
      // Access Token 가져오기
      const accessToken = response.data?.accessToken;

      if (accessToken) {
        // 헬퍼 함수를 사용해서 토큰 저장
        const userInfo = {
          id: response.data.id || username,
          nickname: response.data.nickname,
          email: response.data.email
        };
        
        login(accessToken, userInfo);
        
        console.log('로그인 성공! 메인 페이지로 이동합니다.');
        
        // 메인 페이지로 이동
        navigate('/', { replace: true });
      } else {
        console.error('토큰을 받지 못했습니다:', response.data);
        alert('로그인에 성공했으나 토큰을 받지 못했습니다.');
      }

    } catch (error) {
      console.error('로그인 실패:', error);
      
      if (error.response) {
        // 서버가 응답을 반환한 경우
        const status = error.response.status;
        const message = error.response.data?.message || error.response.data?.error;
        
        if (status === 401 || status === 403) {
          alert(message || '아이디 또는 비밀번호가 일치하지 않습니다.');
        } else if (status === 500) {
          alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        } else {
          alert(message || '로그인 중 오류가 발생했습니다.');
        }
      } else if (error.request) {
        // 요청은 보냈지만 응답을 받지 못한 경우
        alert('서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.');
      } else {
        // 요청 설정 중 오류가 발생한 경우
        alert('로그인 요청 중 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
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
              disabled={isLoading}
              required 
              autoComplete="username"
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input 
              type="password" 
              id="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isLoading}
              required 
              autoComplete="current-password"
            />
          </div>
          <button 
            type="submit" 
            className="login-btn"
            disabled={isLoading}
          >
            {isLoading ? '로그인 중...' : '로그인'}
          </button>
          <Link to="/signup" className="signup-btn-link">
            <button 
              type="button" 
              className="signup-btn"
              disabled={isLoading}
            >
              회원가입
            </button>
          </Link>
          <div className="find-links">
            <Link to="/find-id">아이디 찾기</Link>
            <span className="divider">|</span>
            <Link to="/find-password">비밀번호 찾기</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;