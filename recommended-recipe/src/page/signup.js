import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './SignupPage.css';

function SignupPage() {
  const [formData, setFormData] = useState({
    name: '',
    gender: '',
    age: '',
    email: '',
    userid: '',
    password: '',
    nickname: '',
    number: ''
  });

  // 유효성 검사 및 에러 메시지 상태
  const [passwordMsg, setPasswordMsg] = useState('');
  const [passwordMsgClass, setPasswordMsgClass] = useState('');
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  // 비밀번호와 비밀번호 확인 필드가 변경될 때마다 일치 여부 검사
  useEffect(() => {
    if (!formData.passwordConfirm) {
      setPasswordMsg('');
      setPasswordMsgClass('');
      return;
    }

    if (formData.password && formData.password === formData.passwordConfirm) {
      setPasswordMsg('비밀번호가 일치합니다.');
      setPasswordMsgClass('success');
    } else {
      setPasswordMsg('비밀번호가 일치하지 않습니다.');
      setPasswordMsgClass('error');
    }
  }, [formData.password, formData.passwordConfirm]);


  const handleCheckUserid = () => {
    if (!formData.userid) {
      alert("아이디를 입력해주세요.");
      return;
    }

    if (formData.userid === "admin" || formData.userid === "test") {
      alert("이미 사용 중인 아이디입니다.");
    } else {
      alert("사용 가능한 아이디입니다.");
    }
  };
  
  const handleCheckNickname = () => {
    if (!formData.nickname) {
      alert("닉네임을 입력해주세요.");
      return;
    }

    if (formData.nickname === "admin" || formData.nickname === "test") {
      alert("이미 사용 중인 닉네임입니다.");
    } else {
      alert("사용 가능한 닉네임입니다.");
    }
  };

  const handleSubmit = (e) => {
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
        
    console.log('Form Data Submitted:', formData);
    alert('회원가입 완료!');
    navigate('/');
  };


  return (
    <>
      <div className="header">
        <Link to="/">원룸 레시피</Link>
      </div>

      <div className="signup-container">
        <h2>회원가입</h2>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          {                                                     }
          <div className="input-group">
            <label htmlFor="name">이름</label>
            <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="gender">성별</label>
            <select id="gender" name="gender" value={formData.gender} onChange={handleChange} required>
              <option value="">선택</option>
              <option value="남">남</option>
              <option value="여">여</option>
            </select>
          </div>

          <div className="input-group">
            <label htmlFor="age">나이</label>
            <input type="number" id="age" name="age" min="1" max="120" value={formData.age} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="email">이메일</label>
            <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="userid">아이디</label>
            <div className="nickname-group">
              <input type="text" id="userid" name="userid" value={formData.userid} onChange={handleChange} required />
              <button type="button" onClick={handleCheckUserid}>중복확인</button>
            </div>
          </div>

          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input type="password" id="password" name="password" value={formData.password} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" value={formData.passwordConfirm} onChange={handleChange} required />
            {passwordMsg && <div className={passwordMsgClass}>{passwordMsg}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="nickname">닉네임</label>
            <div className="nickname-group">
              <input type="text" id="nickname" name="nickname" value={formData.nickname} onChange={handleChange} required />
              <button type="button" onClick={handleCheckNickname}>중복확인</button>
            </div>
          </div>

          <button type="submit" className="submit-btn">회원가입</button>
        </form>
      </div>
    </>
  );
}

export default SignupPage;