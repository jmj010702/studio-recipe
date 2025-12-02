// src/page/SignupPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios.js'; // 1. src/api/axios.js 파일을 불러옵니다.
import './signup.css'; // 2. src/page/signup.css 파일을 불러옵니다.

function SignupPage() {
  const [formData, setFormData] = useState({
    name: '',
    gender: '',
    age: '',
    email: '',
    username: '',
    password: '',
    passwordConfirm: '',
    nickname: ''
  });
  
  const [passwordError, setPasswordError] = useState(''); 

  const navigate = useNavigate();

  // 폼 입력 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  // 비밀번호 실시간 오류 검사
  useEffect(() => {
    if (formData.passwordConfirm && formData.password !== formData.passwordConfirm) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordError('');
    }
  }, [formData.password, formData.passwordConfirm]);

  // 회원가입 제출 핸들러
  const handleSignup = async (e) => {
    e.preventDefault();
    
    if (passwordError) {
      alert('비밀번호가 일치하지 않습니다. 확인해주세요.');
      return;
    }

    // --- (Mock) 임시 회원가입 로직 ---
    try {
      const existingUsers = JSON.parse(localStorage.getItem('fake_users') || '[]');
      
      const isUsernameTaken = existingUsers.some(user => user.username === formData.username);
      if (isUsernameTaken) {
        alert('회원가입 중 오류가 발생했습니다. (예: 아이디 또는 이메일 중복)');
        return;
      }
      
      const { passwordConfirm, ...newUser } = formData;
      const updatedUsers = [...existingUsers, newUser];
      localStorage.setItem('fake_users', JSON.stringify(updatedUsers));
      
      alert(' (Mock) 회원가입이 완료되었습니다. 메인 페이지로 이동합니다.');
      navigate('/'); // 메인 페이지로 이동

    } catch (error) {
      console.error('Mock 회원가입 실패:', error);
      alert('임시 회원가입 중 로컬 스토리지 오류가 발생했습니다.');
    }
    // --- (Mock) 로직 끝 ---


    /*
    // (실제 API 호출 로직은 주석 처리)
    try {
      const { passwordConfirm, ...signupData } = formData; 
      await api.post('/api/auth/signup', signupData);
      alert('회원가입이 완료되었습니다. 메인 페이지로 이동합니다.');
      navigate('/');
    } catch (error) {
      console.error('회원가입 실패:', error);
      alert('회원가입 중 오류가 발생했습니다. (예: 아이디 또는 이메일 중복)');
    }
    */
  };

  // 중복 확인 핸들러
  const handleCheckDuplication = (type) => {
    const value = formData[type];
    if (!value) {
      alert(`${type === 'username' ? '아이디' : '닉네임'}를 입력하세요.`);
      return;
    }
    alert(`'${value}'는 사용 가능한 ${type === 'username' ? '아이디' : '닉네임'}입니다. (임시)`);
  };


  return (
    // ... (JSX 렌더링 부분은 동일) ...
    <div className="signup-page-container">
      <div className="signup-box">
        <h2>회원가입</h2>
        <form onSubmit={handleSignup}>
          
          <div className="input-group">
            <label htmlFor="name">이름</label>
            <input type="text" id="name" name="name" onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="gender">성별</label>
            <select id="gender" name="gender" onChange={handleChange} required>
              <option value="" disabled selected>선택</option>
              <option value="MALE">남성</option>
              <option value="FEMALE">여성</option>
              <option value="OTHER">기타</option>
            </select>
          </div>

          <div className="input-group">
            <label htmlFor="age">나이</label>
            <input type="number" id="age" name="age" onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="email">이메일</label>
            <input type="email" id="email" name="email" onChange={handleChange} required />
          </div>

          <div className="input-group with-button">
            <label htmlFor="username">아이디</label>
            <div className="input-wrapper">
              <input type="text" id="username" name="username" onChange={handleChange} required />
              <button 
                type="button" 
                className="check-btn" 
                onClick={() => handleCheckDuplication('username')}
              >
                중복확인
              </button>
            </div>
          </div>

          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input type="password" id="password" name="password" onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" onChange={handleChange} required />
            {passwordError && <p className="error-message">{passwordError}</p>}
          </div>

          <div className="input-group with-button">
            <label htmlFor="nickname">닉네임</label>
            <div className="input-wrapper">
              <input type="text" id="nickname" name="nickname" onChange={handleChange} required />
              <button 
                type="button" 
                className="check-btn" 
                onClick={() => handleCheckDuplication('nickname')}
              >
                중복확인
              </button>
            </div>
          </div>

          <button type="submit" className="signup-submit-btn">
            회원가입
          </button>

        </form>
      </div>
    </div>
  );
}

export default SignupPage;