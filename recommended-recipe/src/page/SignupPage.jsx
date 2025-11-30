// src/page/SignupPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios.js'; 
import './signup.css'; 

function SignupPage() {
  const [formData, setFormData] = useState({
    name: '',
    gender: '', 
    birth: '', 
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
    const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])(?=\S+$).{8,32}$/;
    if (formData.password && !passwordRegex.test(formData.password)) {
      setPasswordError('비밀번호는 8~32자이며, 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.');
    } else if (formData.passwordConfirm && formData.password !== formData.passwordConfirm) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
    } else {
      setPasswordError('');
    }
  }, [formData.password, formData.passwordConfirm]);

  // 회원가입 제출 핸들러
  const handleSignup = async (e) => {
    e.preventDefault();
    
    if (passwordError) {
      alert(passwordError);
      return;
    }

    try {
      // 1. gender 값을 DTO Enum('M'/'F')에 맞게 변환
      let apiGender = '';
      if (formData.gender === 'MALE') {
        apiGender = 'M';
      } else if (formData.gender === 'FEMALE') {
        apiGender = 'F';
      }
      
      if (!apiGender) {
         alert('성별을 선택해주세요.');
         return;
      }

      // 2. DTO 스펙에 맞게 데이터 객체 매핑
      const signupData = {
        name: formData.name.trim(),
        gender: apiGender,
        birth: formData.birth,
        email: formData.email.trim(),
        id: formData.username.trim(),     
        password: formData.password, 
        nickname: formData.nickname.trim()
      };
      
      // 3. ✅ [수정] /auth/signup -> /api/auth/signup
      await api.post('/api/auth/signup', signupData);
      
      alert('회원가입이 완료되었습니다. 메인 페이지로 이동합니다.');
      navigate('/');

    } catch (error) {
      console.error('회원가입 실패:', error);
      if (error.response && error.response.status === 409) {
        alert('이미 사용 중인 아이디, 이메일 또는 닉네임입니다.');
      } else {
        alert('회원가입 중 오류가 발생했습니다.');
      }
    }
  };

  // 중복 확인 핸들러
  const handleCheckDuplication = async (type) => {
    const value = formData[type].trim();
    if (!value) {
      alert(`${type === 'username' ? '아이디' : '닉네임'}를 입력하세요.`);
      return;
    }
    const apiType = type === 'username' ? 'id' : type;

    try {
      // ✅ [수정] /auth/check -> /api/auth/check
      await api.get(`/api/auth/check/${apiType}?value=${value}`);
      alert(`'${value}'는 사용 가능한 ${type === 'username' ? '아이디' : '닉네임'}입니다.`);
    } catch (error) {
      if (error.response && error.response.status === 409) {
        alert(`'${value}'는 이미 사용 중인 ${type === 'username' ? '아이디' : '닉네임'}입니다.`);
      } else {
        alert('중복 확인 중 오류가 발생했습니다.');
      }
    }
  };


  return (
    <div className="signup-page-container">
      <div className="signup-box">
        <h2>회원가입</h2>
        <form onSubmit={handleSignup}>
          
          <div className="input-group">
            <label htmlFor="name">이름</label>
            <input type="text" id="name" name="name" onChange={handleChange} value={formData.name} required />
          </div>

          <div className="input-group">
            <label htmlFor="gender">성별</label>
            <select id="gender" name="gender" onChange={handleChange} value={formData.gender} required>
              <option value="">선택</option>
              <option value="MALE">남성</option>
              <option value="FEMALE">여성</option>
            </select>
          </div>

          <div className="input-group">
            <label htmlFor="birth">생년월일</label>
            <input 
              type="date" 
              id="birth" 
              name="birth" 
              onChange={handleChange} 
              value={formData.birth} 
              required 
            />
          </div>

          <div className="input-group">
            <label htmlFor="email">이메일</label>
            <input type="email" id="email" name="email" onChange={handleChange} value={formData.email} required />
          </div>

          <div className="input-group with-button">
            <label htmlFor="username">아이디 (8~16자)</label>
            <div className="input-wrapper">
              <input type="text" id="username" name="username" onChange={handleChange} value={formData.username} required />
              <button type="button" className="check-btn" onClick={() => handleCheckDuplication('username')}>중복확인</button>
            </div>
          </div>

          <div className="input-group">
            <label htmlFor="password">비밀번호 (영문 대소문자, 숫자, 특수문자 포함 8~32자)</label>
            <input type="password" id="password" name="password" onChange={handleChange} value={formData.password} required />
          </div>

          <div className="input-group">
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" onChange={handleChange} value={formData.passwordConfirm} required />
            {passwordError && <p className="error-message">{passwordError}</p>}
          </div>

          <div className="input-group with-button">
            <label htmlFor="nickname">닉네임</label>
            <div className="input-wrapper">
              <input type="text" id="nickname" name="nickname" onChange={handleChange} value={formData.nickname} required />
              <button type="button" className="check-btn" onClick={() => handleCheckDuplication('nickname')}>중복확인</button>
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