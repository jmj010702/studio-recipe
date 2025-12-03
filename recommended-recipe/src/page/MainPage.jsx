// src/page/MyPage.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  FaThList,
  FaStar,
  FaCommentDots,
  FaBookOpen,
  FaSearch,
} from 'react-icons/fa';
import api from '../api/axios';
import './MyPage.css';

function MyPage() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  // 1차: sessionStorage로 로그인 여부 체크
  useEffect(() => {
    const userSession = sessionStorage.getItem('logged_in_user');
    if (userSession) {
      setUser(JSON.parse(userSession));
    } else {
      alert('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [navigate]);

  // 2차: 백엔드에서 최신 유저 정보 가져와서 동기화
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await api.get('auth/me');
        setUser(response.data);
        sessionStorage.setItem(
          'logged_in_user',
          JSON.stringify(response.data),
        );
      } catch (error) {
        console.error('유저 정보를 불러오는 중 오류:', error);
      }
    };

    fetchUser();
  }, []);

  if (!user) {
    return <div>로딩 중...</div>;
  }

  return (
    <div className="mypage-container">
      <nav className="mypage-nav">
        <button className="nav-item active">
          <FaThList /> 레시피
        </button>
        <button className="nav-item">
          <FaStar /> 나의 냉장고 등록하기
        </button>
        <button className="nav-item">
          <FaCommentDots /> 댓글
        </button>
        <button className="nav-item">
          <FaBookOpen /> 스토리
        </button>
      </nav>

      <div className="mypage-content">
        <div className="tabs">
          <span className="tab-item active">공개중</span>
          <span className="tab-item">작성중</span>
        </div>

        <div className="empty-state">
          <div className="profile-pic">
            {user.nickname
              ? user.nickname.charAt(0).toUpperCase()
              : user.username
              ? user.username.charAt(0).toUpperCase()
              : 'N'}
          </div>
          <h3>레시피를 직접 올려보세요!</h3>
          <p>
            자랑하고 싶은 나만의 레시피! 공유하고 싶은 멋진 레시피를 올려 주세요.
          </p>
          <Link to="/recipe/write" className="register-btn">
            레시피 등록하기
          </Link>
        </div>

        <div className="recipe-search">
          <input type="text" placeholder="레시피 검색" />
          <button>
            <FaSearch />
          </button>
        </div>
      </div>
    </div>
  );
}

export default MyPage;
