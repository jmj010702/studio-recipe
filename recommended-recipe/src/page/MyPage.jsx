import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaThList, FaStar, FaCommentDots, FaBookOpen, FaSearch } from 'react-icons/fa';
import './MyPage.css'; 

function MyPage() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const userSession = sessionStorage.getItem('logged_in_user');
    if (userSession) {
      setUser(JSON.parse(userSession));
    } else {
      alert('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [navigate]);

  if (!user) {
    return <div>로딩 중...</div>; 
  }

  return (
    <div className="mypage-container">
      {/* 1. 상단 회색 네비게이션 바 */}
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

      {/* 2. 메인 콘텐츠 영역 */}
      <div className="mypage-content">
        {/* 공개중 / 작성중 탭 */}
        <div className="tabs">
          <span className="tab-item active">공개중</span>
          <span className="tab-item">작성중</span>
        </div>

        {/* 3. 레시피 없음 (Empty State) */}
        <div className="empty-state">
          <div className="profile-pic">
            {/* user.nickname의 첫 글자 표시 (예시) */}
            {user.nickname ? user.nickname.charAt(0).toUpperCase() : 'N'}
          </div>
          <h3>레시피를 직접 올려보세요!</h3>
          <p>자랑하고 싶은 나만의 레시피! 공유하고 싶은 멋진 레시피를 올려 주세요.</p>
          <Link to="/recipe/write" className="register-btn">
            레시피 등록하기
          </Link>
        </div>

        {/* 4. 하단 레시피 검색 */}
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