import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios'; 
import './login.css'; 

function FindPasswordPage() {
  const [email, setEmail] = useState(''); // username -> email로 변경 (명확하게)
  const [name, setName] = useState('');
  
  const [authKey, setAuthKey] = useState(''); 
  const [isKeySent, setIsKeySent] = useState(false); 
  const [isVerified, setIsVerified] = useState(false); 
  
  // 토큰 저장용 (비밀번호 변경 시 필요)
  const [resetToken, setResetToken] = useState(''); 

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const navigate = useNavigate();

  // 비밀번호 일치 여부 검사
  useEffect(() => {
    if (isVerified && confirmPassword && newPassword !== confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordError('');
    }
  }, [isVerified, newPassword, confirmPassword]);

  // --- 1. 인증번호 발송 핸들러 ---
  const handleSendKey = async (e) => {
    e.preventDefault();
    if (!email) {
      alert('아이디(이메일)를 입력해주세요.');
      return;
    }
    
    try {
      // ✅ [수정] 백엔드 주소: /auth/send-verification
      await api.post('/auth/send-verification', { 
        email: email 
        // 백엔드 EmailRequest DTO에는 name 필드가 없을 수 있어 생략 가능하지만 보내도 무방
      });
      
      alert('인증번호가 발송되었습니다. 이메일을 확인해주세요.');
      setIsKeySent(true); 

    } catch (error) {
      console.error('인증키 발송 실패:', error);
      alert('사용자 정보를 찾을 수 없거나 발송에 실패했습니다.');
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
      // ✅ [수정] 백엔드 주소: /auth/verify-code
      // DTO: VerifyCodeRequest (email, verificationCode, purpose)
      const response = await api.post('/auth/verify-code', { 
        email: email, 
        verificationCode: authKey,
        purpose: 'RESET_PASSWORD' // 백엔드에서 필요로 할 수 있음 (없으면 무시됨)
      });
      
      // 백엔드가 성공 시 resetToken을 줍니다. (ResetProcessResponse)
      const token = response.data.resetToken;
      if (token) {
        setResetToken(token); // 토큰 저장 중요!
        alert('인증이 완료되었습니다. 새 비밀번호를 입력하세요.');
        setIsVerified(true);
      } else {
        alert('인증은 되었으나 토큰을 받지 못했습니다.');
      }

    } catch (error) {
      console.error('인증키 확인 실패:', error);
      alert('인증키가 올바르지 않거나 만료되었습니다.');
    }
  };

  // --- 3. 최종 비밀번호 변경 핸들러 ---
  const handleSubmit = async (e) => {
    e.preventDefault(); 
    
    if (passwordError) {
      alert(passwordError);
      return;
    }

    if (!resetToken) {
      alert('인증 토큰이 없습니다. 처음부터 다시 시도해주세요.');
      return;
    }

    try {
      // ✅ [수정] 백엔드 주소: /auth/reset-password
      // DTO: ResetPasswordRequest (token, newPassword)
      await api.post('/auth/reset-password', {
        token: resetToken, // 아까 받은 토큰
        newPassword: newPassword
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
          
          {/* --- 1단계: 사용자 정보 입력 --- */}
          {!isVerified && (
            <>
              <div className="input-group">
                <label htmlFor="username">e-mail</label>
                <input 
                  type="text" 
                  id="username" 
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={isKeySent} 
                  required 
                  placeholder="예: aaa@aaa.com"
                />
              </div>
              
              {/* 이름 입력칸은 백엔드 로직상 필수 아닐 수 있으나 UI 유지 */}
              <div className="input-group">
                <label htmlFor="name">이름</label>
                <input 
                  type="text" 
                  id="name" 
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  disabled={isKeySent} 
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

          {/* --- 2단계: 인증키 입력 --- */}
          {isKeySent && !isVerified && (
            <div className="input-group" style={{ marginTop: '20px', borderTop: '1px solid #eee', paddingTop: '20px' }}>
              <label htmlFor="authKey">인증키 입력</label>
              <input 
                type="text" 
                id="authKey" 
                value={authKey}
                onChange={(e) => setAuthKey(e.target.value)}
                required 
                placeholder="이메일로 받은 번호 입력"
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

          {/* --- 3단계: 새 비밀번호 입력 --- */}
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