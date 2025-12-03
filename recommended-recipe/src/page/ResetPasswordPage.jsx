// src/page/ResetPasswordPage.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios'; // 👈 API 연동
import './login.css'; // 👈 로그인 CSS 재사용

function ResetPasswordPage() {
  const [username, setUsername] = useState('');
  const [name, setName] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [passwordError, setPasswordError] = useState('');
  const navigate = useNavigate();

  // (SignupPage와 동일) 비밀번호 실시간 유효성 검사
  useEffect(() => {
    if (newPassword && newPassword.length < 8) {
      setPasswordError('비밀번호는 8자 이상이어야 합니다.');
    } else if (confirmPassword && newPassword !== confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordError('');
    }
  }, [newPassword, confirmPassword]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (passwordError) {
      alert(passwordError);
      return;
    }

    try {
      // 1. 💡 백엔드에 정보 확인 및 비밀번호 변경 요청
      await api.post('/api/reset-password', {
        username, // (또는 email)
        name,
        newPassword
      });

      // 2. 💡 성공 시
      alert('비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.');
      navigate('/login');
      
    } catch (error) {
      // 3. 💡 실패 시 (404 Not Found 등)
      console.error('비밀번호 재설정 실패:', error);
      if (error.response && error.response.status === 404) {
        alert('입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.');
      } else {
        alert('비밀번호 재설정 중 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-box">
        <h2>비밀번호 재설정</h2>
        <form onSubmit={handleSubmit}>
          
          <div className="input-group">
            <label htmlFor="username">아이디(이메일)</label>
            <input 
              type="text" 
              id="username" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required 
            />
          </div>
          
          <div className="input-group">
            <label htmlFor="name">이름</label>
            <input 
              type="text" 
              id="name" 
              value={name}
              onChange={(e) => setName(e.target.value)}
              required 
            />
          </div>
          
          <div className="input-group">
            <label htmlFor="newPassword">새 비밀번호 (8자 이상)</label>
            <input 
              type="password" 
              id="newPassword" 
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required 
            />
          </div>
          
          <div className="input-group">
            <label htmlFor="confirmPassword">새 비밀번호 확인</label>
            <input 
              type="password" 
              id="confirmPassword" 
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required 
            />
          </div>

          {/* --- 💡 비밀번호 오류 메시지 표시 --- */}
          {passwordError && (
            <p className="error-message" style={{ color: 'red', textAlign: 'center', marginBottom: '10px' }}>
              {passwordError}
            </p>
          )}
          
          <button type="submit" className="login-btn" style={{ marginTop: '20px' }}>
            비밀번호 변경
          </button>

          <div className="find-links" style={{ marginTop: '30px' }}>
            <Link to="/login">로그인으로 돌아가기</Link>
          </div>

        </form>
      </div>
    </div>
  );
}

export default ResetPasswordPage;