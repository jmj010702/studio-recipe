import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import './LoginPage.css';

function LoginPage() {
  const [userid, setUserid] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(''); 

  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    setError(''); 

    if (userid === "test" && password === "1234") {
      navigate('/mypage');
    } else {
      setError('아이디 또는 비밀번호가 올바르지 않습니다.');
    }
  };

  return (

    <>
      <div className="header">
        {                       }
        <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
          원룸 레시피
        </Link>
      </div>

      <div className="login-container">
        <h2>로그인</h2>
        
        {                           }
        {error && <div className="error-msg">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            {                           }
            <label htmlFor="userid">아이디</label>
            <input
              type="text"
              id="userid"
              name="userid"
              value={userid}
              onChange={(e) => setUserid(e.target.value)} 
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              value={password} 
              onChange={(e) => setPassword(e.target.value)} 
              required
            />
          </div>
          <button type="submit">로그인</button>
        </form>
        <button className="signup-btn" onClick={() => navigate('/signup')}>
          회원가입
        </button>
      </div>
    </>
  );
}

export default LoginPage;