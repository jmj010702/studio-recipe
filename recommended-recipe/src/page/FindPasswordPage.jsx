// src/page/FindPasswordPage.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios'; 
import './login.css'; 

function FindPasswordPage() {
  const [username, setUsername] = useState('');
  const [name, setName] = useState('');
  
  const [authKey, setAuthKey] = useState(''); 
  const [isKeySent, setIsKeySent] = useState(false); 
  const [isVerified, setIsVerified] = useState(false); 

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const navigate = useNavigate();

  // --- 💡 [수정] 비밀번호 실시간 유효성 검사 (길이 조건 제거) ---
  useEffect(() => {
    // "8자 이상" 조건을 제거하고, "일치" 여부만 확인합니다.
    if (isVerified && confirmPassword && newPassword !== confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordError('');
    }
  }, [isVerified, newPassword, confirmPassword]); // 👈 [수정] 의존성 배열은 그대로 둠

  // --- 1. 인증번호 발송 핸들러 ---
  const handleSendKey = async (e) => {
    e.preventDefault();
    if (!username || !name) {
      alert('아이디(이메일)와 이름을 모두 입력해주세요.');
      return;
    }
    
    try {
      // 💡 TODO: 백엔드의 "인증키 발송" API 호출
      await api.post('/api/password/send-key', { username, name });
      
      alert('인증번호가 발송되었습니다. 이메일을 확인해주세요.');
      setIsKeySent(true); // 👈 2단계(인증키 입력) UI로 변경

    } catch (error) {
      console.error('인증키 발송 실패:', error);
      alert('사용자 정보를 찾을 수 없습니다.');
    }
  };

  // --- 2. 인증키 확인 핸들러 ---
  const handleVerifyKey = async (e) => {
    e.preventDefault();
    if (!authKey) {
      alert('인증키를 입력해주세요.');
      return;
    }

    try {
      // 💡 TODO: 백엔드의 "인증키 확인" API 호출
      await api.post('/api/password/verify-key', { username, authKey });
      
      alert('인증이 완료되었습니다. 새 비밀번호를 입력하세요.');
      setIsVerified(true); // 👈 3단계(비밀번호 변경) UI로 변경

    } catch (error) {
      console.error('인증키 확인 실패:', error);
      alert('인증키가 올바르지 않습니다.');
    }
  };

  // --- 3. 최종 비밀번호 변경 핸들러 ---
  const handleSubmit = async (e) => {
    e.preventDefault(); 
    
    if (passwordError) {
      alert(passwordError);
      return;
    }

    try {
      // 💡 TODO: 백엔드의 "비밀번호 변경" API 호출
      await api.post('/api/password/reset', {
        username,
        name,
        newPassword
      });

      alert('비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다.');
      navigate('/login');
      
    } catch (error) {
      console.error('비밀번호 재설정 실패:', error);
      alert('비밀번호 재설정 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-box">
        <h2>비밀번호 찾기</h2>
        
        <form onSubmit={handleSubmit}>
          
          {/* --- 1단계: 사용자 정보 입력 (인증 전까지 보임) --- */}
          {!isVerified && (
            <>
              <div className="input-group">
                <label htmlFor="username">아이디(이메일)</label>
                <input 
                  type="text" 
                  id="username" 
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  disabled={isKeySent} 
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
                  disabled={isKeySent} 
                  required 
                />
              </div>

              {!isKeySent && (
                <button 
                  type="button" 
                  className="login-btn" 
                  onClick={handleSendKey}
                >
                  인증번호 발송
                </button>
              )}
            </>
          )}

          {/* --- 2단계: 인증키 입력 (키 발송 후 & 인증 전까지 보임) --- */}
          {isKeySent && !isVerified && (
            <div className="input-group" style={{ marginTop: '20px', borderTop: '1px solid #eee', paddingTop: '20px' }}>
              <label htmlFor="authKey">인증키 입력</label>
              <input 
                type="text" 
                id="authKey" 
                value={authKey}
                onChange={(e) => setAuthKey(e.target.value)}
                required 
              />
              <button 
                type="button" 
                className="login-btn" 
                style={{ backgroundColor: '#6c757d', marginTop: '10px' }}
                onClick={handleVerifyKey}
              >
                인증 확인
              </button>
            </div>
          )}

          {isVerified && (
            <>
              <div className="input-group" style={{ borderTop: '1px solid #eee', paddingTop: '20px' }}>
                <label htmlFor="newPassword">새 비밀번호</label>
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

              {passwordError && (
                <p className="error-message" style={{ color: 'red', textAlign: 'center', marginBottom: '10px' }}>
                  {passwordError}
                </p>
              )}
              
              <button type="submit" className="login-btn">
                비밀번호 변경
              </button>
            </>
          )}

          <div className="find-links" style={{ marginTop: '30px' }}>
            <Link to="/login">로그인으로 돌아가기</Link>
          </div>

        </form>
      </div>
    </div>
  );
}

export default FindPasswordPage;