// src/page/MyPage.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaThList, FaStar, FaCommentDots, FaBookOpen, FaSearch, FaHeart } from 'react-icons/fa';
import api from '../api/axios'; 
import './MyPage.css'; 

function MyPage() {
  const navigate = useNavigate();
  
  const [userInfo, setUserInfo] = useState(null); 
  const [likedList, setLikedList] = useState([]); 
  const [savedList, setSavedList] = useState([]);
  const [authoredList, setAuthoredList] = useState([]); 
  
  const [activeMenu, setActiveMenu] = useState('editProfile');
  const [subTab, setSubTab] = useState('draft');
  
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return; 
    }

    const fetchMyPageData = async () => {
      try {
        const response = await api.get('/api/mypages/me'); 
        const data = response.data;
        
        console.log('API 응답:', data);

        setUserInfo(data.userInfo);
        const liked = data.likedList || [];
        setLikedList(liked);
        
        // savedList도 liked와 동일하게 설정 (좋아요 = 찜하기)
        setSavedList(liked);
        setAuthoredList(data.authoredList || []);
        
        console.log('좋아요한 레시피:', liked.length, '개');
      
      } catch (error) {
        console.error('마이페이지 정보 조회 실패:', error);
        alert('정보를 불러오는 데 실패했습니다. 다시 로그인해주세요.');
        localStorage.removeItem('accessToken'); 
        navigate('/login');
      }
    };
    
    fetchMyPageData();
  }, [navigate]); 

  const handleGoToWritePage = () => {
    navigate('/recipe/write'); 
  };

  const handleRecipeClick = (recipeId) => {
    navigate(`/recipe/${recipeId}`);
  };
  
  if (!userInfo) {
    return <div className="loading-container">마이페이지 정보를 불러오는 중...</div>; 
  }
  
  const renderContent = () => {
    switch (activeMenu) {
      
      case 'editProfile':
        return (
          <div className="profile-edit-container">
            <h2>회원정보 수정</h2>
            <div className="profile-form">
              
              <div className="form-row">
                <span className="form-label">아이디(이메일)</span>
                <div className="form-value-wrapper">
                  <span className="form-value">{userInfo.email}</span> 
                  <button type="button" className="btn-inline">이메일 변경</button>
                </div>
              </div>
              <div className="form-row">
                <span className="form-label">이름</span>
                <div className="form-value-wrapper">
                  <span className="form-value">{userInfo.name}</span>
                </div>
              </div>

              <div className="form-row"> 
                <span className="form-label">닉네임</span>
                <div className="form-value-wrapper">
                  <span className="form-value">{userInfo.nickname}</span>
                </div>
              </div>

              <div className="form-row">
                <span className="form-label">비밀번호 변경</span>
                <div className="form-value-wrapper vertical-inputs">
                  <input type="password" placeholder="현재 비밀번호" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} />
                  <input type="password" placeholder="새 비밀번호" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
                  <input type="password" placeholder="비밀번호 다시 입력" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
                  <button type="button" className="btn-full-width">비밀번호 변경</button>
                </div>
              </div>
            </div>
            <div className="form-actions">
              <button type="button" className="btn-secondary">나가기</button>
              <button type="button" className="btn-danger">회원탈퇴</button>
            </div>
          </div>
        );

      case 'myIngredients':
        return (
          <div className="my-ingredients-container">
            <h2><FaStar /> 나의 냉장고 재료</h2>
            <p>내가 가진 재료를 등록하고 관리합니다.</p>
          </div>
        );

      case 'registerRecipe':
      case 'myAuthoredRecipes':
        return (
          <>
            <div className="tabs">
              <span className={`tab-item ${subTab === 'public' ? 'active' : ''}`} onClick={() => setSubTab('public')}>공개중</span>
              <span className={`tab-item ${subTab === 'draft' ? 'active' : ''}`} onClick={() => setSubTab('draft')}>작성중</span>
            </div>
            {subTab === 'draft' && (
              <div className="empty-state">
                <div className="profile-pic">{userInfo.nickname.charAt(0).toUpperCase()}</div>
                <h3>레시피를 직접 올려보세요!</h3>
                <p>자랑하고 싶은 나만의 레시피! 공유하고 싶은 멋진 레시피를 올려 주세요.</p>
                <button className="register-btn" onClick={handleGoToWritePage}>레시피 등록하기</button>
              </div>
            )}
            {subTab === 'public' && ( 
              <div className="recipes-grid-container">
                <p className="recipe-count">공개 레시피: {authoredList.length}개</p>
                {authoredList.length === 0 ? (
                  <div className="empty-message">
                    <p>작성한 레시피가 없습니다.</p>
                  </div>
                ) : (
                  <div className="recipes-grid">
                    {authoredList.map(recipe => (
                      <div key={recipe.recipeId} className="recipe-card" onClick={() => handleRecipeClick(recipe.recipeId)}>
                        <div className="recipe-image-wrapper">
                          <img src={recipe.imageUrl} alt={recipe.title} />
                        </div>
                        <div className="recipe-info">
                          <h4>{recipe.title}</h4>
                          <div className="recipe-stats">
                            <span>👁️ {recipe.viewCount || 0}</span>
                            <span>❤️ {recipe.likeCount || 0}</span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div> 
            )}
            <div className="recipe-search">
              <input type="text" placeholder="내가 작성한 레시피 검색" />
              <button><FaSearch /></button>
            </div>
          </>
        );

      case 'likedRecipes':
        return (
          <div className="recipes-list-container">
            <h2><FaHeart className="icon-heart" /> 좋아요 누른 레시피</h2>
            <p className="recipe-count">좋아요 누른 레시피: {likedList.length}개</p>
            
            {likedList.length === 0 ? (
              <div className="empty-message">
                <FaHeart className="empty-icon" />
                <p>좋아요 누른 레시피가 없습니다.</p>
                <p className="sub-message">마음에 드는 레시피에 좋아요를 눌러보세요!</p>
              </div>
            ) : (
              <div className="recipes-grid">
                {likedList.map(recipe => (
                  <div key={recipe.recipeId} className="recipe-card" onClick={() => handleRecipeClick(recipe.recipeId)}>
                    <div className="recipe-image-wrapper">
                      <img src={recipe.imageUrl} alt={recipe.title} />
                      <div className="like-badge">
                        <FaHeart />
                      </div>
                    </div>
                    <div className="recipe-info">
                      <h4>{recipe.title}</h4>
                      <div className="recipe-stats">
                        <span>👁️ {recipe.viewCount || 0}</span>
                        <span>❤️ {recipe.likeCount || 0}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        );

      case 'savedRecipes':
        return (
          <div className="recipes-list-container">
            <h2><FaStar className="icon-star" /> 찜한 레시피</h2>
            <p className="recipe-count">찜한 레시피: {savedList.length}개</p>
            
            {savedList.length === 0 ? (
              <div className="empty-message">
                <FaStar className="empty-icon" />
                <p>찜한 레시피가 없습니다.</p>
                <p className="sub-message">나중에 만들어볼 레시피를 찜해보세요!</p>
              </div>
            ) : (
              <div className="recipes-grid">
                {savedList.map(recipe => (
                  <div key={recipe.recipeId} className="recipe-card" onClick={() => handleRecipeClick(recipe.recipeId)}>
                    <div className="recipe-image-wrapper">
                      <img src={recipe.imageUrl} alt={recipe.title} />
                      <div className="saved-badge">
                        <FaStar />
                      </div>
                    </div>
                    <div className="recipe-info">
                      <h4>{recipe.title}</h4>
                      <div className="recipe-stats">
                        <span>👁️ {recipe.viewCount || 0}</span>
                        <span>❤️ {recipe.likeCount || 0}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        );
        
      case 'iconMenu':
        return (
          <div className="icon-menu-container">
            <h2><FaBookOpen /> 아이콘 메뉴</h2>
          </div>
        );

      default:
        return <div className="profile-edit-container"><h2>나의 정보 수정</h2></div>;
    }
  };

  return (
    <div className="mypage-container">
      <nav className="mypage-nav">
        <button className={`nav-item ${activeMenu === 'editProfile' ? 'active' : ''}`} onClick={() => setActiveMenu('editProfile')}>
          <FaThList /> 나의 정보 수정
        </button>
        <button className={`nav-item ${activeMenu === 'myIngredients' ? 'active' : ''}`} onClick={() => setActiveMenu('myIngredients')}>
          <FaStar /> 나의 냉장고 재료 등록하기
        </button>
        <button className={`nav-item ${activeMenu === 'registerRecipe' ? 'active' : ''}`} onClick={() => setActiveMenu('registerRecipe')}>
          <FaCommentDots /> 레시피 등록하기 
        </button>
        <button className={`nav-item ${activeMenu === 'likedRecipes' ? 'active' : ''}`} onClick={() => setActiveMenu('likedRecipes')}>
          <FaHeart /> 좋아요 누른 레시피들 
        </button>
        <button className={`nav-item ${activeMenu === 'savedRecipes' ? 'active' : ''}`} onClick={() => setActiveMenu('savedRecipes')}>
          <FaStar /> 찜한 레시피들 
        </button>
        <button className={`nav-item ${activeMenu === 'myAuthoredRecipes' ? 'active' : ''}`} onClick={() => setActiveMenu('myAuthoredRecipes')}>
          <FaBookOpen /> 내가 작성한 레시피
        </button>
        <button className={`nav-item ${activeMenu === 'iconMenu' ? 'active' : ''}`} onClick={() => setActiveMenu('iconMenu')}>
          <FaBookOpen /> 
        </button>
      </nav>

      <div className="mypage-content">
        {renderContent()}
      </div>
    </div>
  );
}

export default MyPage;