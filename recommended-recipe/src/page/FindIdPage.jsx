// src/page/FindIdPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios'; // 👈 API 연동
import './login.css'; // 👈 로그인 CSS 재사용

function FindIdPage() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  
  // 💡 API 응답 메시지(성공 또는 실패)를 저장할 상태
  const [resultMessage, setResultMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setResultMessage(''); // 메시지 초기화

    try {
      // 1. 💡 백엔드에 이름과 이메일로 아이디(username) 요청
      const response = await api.post('/api/find-id', { name, email });

      // 2. 💡 성공 시 (백엔드가 { username: "..." } 형태를 반환한다고 가정)
      setResultMessage(`회원님의 아이디는 [ ${response.data.username} ] 입니다.`);
      
    } catch (error) {
      // 3. 💡 실패 시 (404 Not Found 등)
      console.error('아이디 찾기 실패:', error);
      if (error.response && error.response.status === 404) {
        setResultMessage('입력하신 정보와 일치하는 사용자를 찾을 수 없습니다.');
      } else {
        setResultMessage('아이디 찾기 중 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-box">
        <h2>아이디 찾기</h2>
        <form onSubmit={handleSubmit}>
          
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
            <label htmlFor="email">이메일</label>
            <input 
              type="email" 
              id="email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required 
            />
          </div>
          
          <button type="submit" className="login-btn" style={{ marginTop: '20px' }}>
            아이디 찾기
          </button>

          {/* --- 💡 API 결과 메시지 표시 --- */}
          {resultMessage && (
            <div className="result-message" style={{ marginTop: '20px', textAlign: 'center' }}>
              {resultMessage}
            </div>
          )}

          <div className="find-links" style={{ marginTop: '30px' }}>
            <Link to="/login">로그인으로 돌아가기</Link>
          </div>

        </form>
      </div>
    </div>
  );
}

export default FindIdPage;