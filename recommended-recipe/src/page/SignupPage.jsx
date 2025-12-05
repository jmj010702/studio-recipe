// src/page/SignupPage.jsx
import React, { useState, useEffect } from 'react';
<<<<<<< HEAD
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios.js';
import './signup.css';

function SignupPage() {
  const [formData, setFormData] = useState({
    userid: '',
=======
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
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
    password: '',
    passwordConfirm: '',
    name: '',
    nickname: '',
    email: '',
    gender: '',
    age: '',
  });
<<<<<<< HEAD

  const [passwordMsg, setPasswordMsg] = useState('');
  const [passwordMsgClass, setPasswordMsgClass] = useState('');
  const [error, setError] = useState('');

=======
  
  const [passwordError, setPasswordError] = useState(''); 
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  useEffect(() => {
<<<<<<< HEAD
    if (!formData.passwordConfirm) {
      setPasswordMsg('');
      setPasswordMsgClass('');
      return;
    }

    if (formData.password && formData.password === formData.passwordConfirm) {
      setPasswordMsg('비밀번호가 일치합니다.');
      setPasswordMsgClass('success');
=======
    const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])(?=\S+$).{8,32}$/;
    if (formData.password && !passwordRegex.test(formData.password)) {
      setPasswordError('비밀번호는 8~32자이며, 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.');
    } else if (formData.passwordConfirm && formData.password !== formData.passwordConfirm) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
    } else {
      setPasswordMsg('비밀번호가 일치하지 않습니다.');
      setPasswordMsgClass('error');
    }
  }, [formData.password, formData.passwordConfirm]);

<<<<<<< HEAD
  const handleCheckUserid = () => {
    if (!formData.userid) {
      alert('아이디를 입력해주세요.');
      return;
    }

    if (formData.userid === 'admin' || formData.userid === 'test') {
      alert('이미 사용 중인 아이디입니다.');
    } else {
      alert('사용 가능한 아이디입니다. (임시)');
    }
  };

  const handleCheckNickname = () => {
    if (!formData.nickname) {
      alert('닉네임을 입력해주세요.');
      return;
    }

    if (formData.nickname === 'admin' || formData.nickname === 'test') {
      alert('이미 사용 중인 닉네임입니다.');
    } else {
      alert('사용 가능한 닉네임입니다. (임시)');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    for (const key in formData) {
      if (!formData[key]) {
        setError('모든 필드를 입력해주세요.');
        return;
      }
    }

    if (formData.password !== formData.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const { passwordConfirm, ...payload } = formData;
      await api.post('auth/registry', payload);

      alert('회원가입 완료!');
      navigate('/login');
    } catch (err) {
      console.error('회원가입 실패:', err);
      setError('회원가입 중 오류가 발생했습니다.');
=======
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
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
    }
  };

  return (
<<<<<<< HEAD
    <>
      <div className="header">
        <Link to="/">원룸 레시피</Link>
      </div>

      <div className="signup-container">
=======
    <div className="signup-page-container">
      <div className="signup-box">
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
        <h2>회원가입</h2>
        {error && <div className="error-msg">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="name">이름</label>
<<<<<<< HEAD
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
=======
            <input type="text" id="name" name="name" onChange={handleChange} value={formData.name} required />
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
          </div>

          <div className="input-group">
            <label htmlFor="gender">성별</label>
<<<<<<< HEAD
            <select
              id="gender"
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              required
            >
              <option value="">선택</option>
              <option value="남">남</option>
              <option value="여">여</option>
=======
            <select id="gender" name="gender" onChange={handleChange} value={formData.gender} required>
              <option value="">선택</option>
              <option value="MALE">남성</option>
              <option value="FEMALE">여성</option>
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
            </select>
          </div>

          <div className="input-group">
<<<<<<< HEAD
            <label htmlFor="age">나이</label>
            <input
              type="number"
              id="age"
              name="age"
              min="1"
              max="120"
              value={formData.age}
              onChange={handleChange}
              required
=======
            <label htmlFor="birth">생년월일</label>
            <input 
              type="date" 
              id="birth" 
              name="birth" 
              onChange={handleChange} 
              value={formData.birth} 
              required 
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
            />
          </div>

          <div className="input-group">
            <label htmlFor="email">이메일</label>
<<<<<<< HEAD
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="input-group">
            <label htmlFor="userid">아이디</label>
            <div className="nickname-group">
              <input
                type="text"
                id="userid"
                name="userid"
                value={formData.userid}
                onChange={handleChange}
                required
              />
              <button type="button" onClick={handleCheckUserid}>
                중복확인
              </button>
=======
            <input type="email" id="email" name="email" onChange={handleChange} value={formData.email} required />
          </div>

          <div className="input-group with-button">
            <label htmlFor="username">아이디 (8~16자)</label>
            <div className="input-wrapper">
              <input type="text" id="username" name="username" onChange={handleChange} value={formData.username} required />
              <button type="button" className="check-btn" onClick={() => handleCheckDuplication('username')}>중복확인</button>
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
            </div>
          </div>

          <div className="input-group">
<<<<<<< HEAD
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
=======
            <label htmlFor="password">비밀번호 (영문 대소문자, 숫자, 특수문자 포함 8~32자)</label>
            <input type="password" id="password" name="password" onChange={handleChange} value={formData.password} required />
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
          </div>

          <div className="input-group">
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
<<<<<<< HEAD
            <input
              type="password"
              id="passwordConfirm"
              name="passwordConfirm"
              value={formData.passwordConfirm || ''}
              onChange={handleChange}
              required
            />
            {passwordMsg && (
              <div className={passwordMsgClass}>{passwordMsg}</div>
            )}
=======
            <input type="password" id="passwordConfirm" name="passwordConfirm" onChange={handleChange} value={formData.passwordConfirm} required />
            {passwordError && <p className="error-message">{passwordError}</p>}
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
          </div>

          <div className="input-group">
            <label htmlFor="nickname">닉네임</label>
<<<<<<< HEAD
            <div className="nickname-group">
              <input
                type="text"
                id="nickname"
                name="nickname"
                value={formData.nickname}
                onChange={handleChange}
                required
              />
              <button type="button" onClick={handleCheckNickname}>
                중복확인
              </button>
=======
            <div className="input-wrapper">
              <input type="text" id="nickname" name="nickname" onChange={handleChange} value={formData.nickname} required />
              <button type="button" className="check-btn" onClick={() => handleCheckDuplication('nickname')}>중복확인</button>
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
            </div>
          </div>

          <button type="submit" className="submit-btn">
            회원가입
          </button>
        </form>
      </div>
    </>
  );
}

export default SignupPage;
