// src/page/LoginPage.jsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './login.css';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    const inputUsername = username.trim();
    const inputPassword = password.trim();

    if (!inputUsername || !inputPassword) {
      setError('아이디와 비밀번호를 모두 입력하세요.');
      return;
    }

    try {
      const response = await api.post('auth/login', {
        username: inputUsername,
        password: inputPassword,
      });

      const loggedInUser = response.data;
      sessionStorage.setItem('logged_in_user', JSON.stringify(loggedInUser));

      navigate('/');
    } catch (err) {
      console.error('로그인 요청 실패:', err);
      setError('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-box">
        <h2>로그인</h2>

        {error && <div className="error-msg">{error}</div>}

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
