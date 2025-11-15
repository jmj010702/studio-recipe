import React, { useState, useEffect } from 'react';
import api, { isAuthenticated } from '../api/axios.js'; 
import Navigation from '../components/Navigation.jsx';
import RecipeSection from '../components/RecipeSection.jsx';
import './MainPage.css'; 

function MainPage() {
  const [todayRecipes, setTodayRecipes] = useState([]);
  const [topRecipes, setTopRecipes] = useState([]);
  const [likedRecipes, setLikedRecipes] = useState([]);
  const [bookmarkedRecipes, setBookmarkedRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  
  // 로그인 상태 확인
  useEffect(() => {
    setIsLoggedIn(isAuthenticated());
  }, []);
  
  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // 기본 레시피 데이터 가져오기
        const response = await api.get('/api/mainPages');
        const data = response.data?.data || {};
        const recommended = data['recommended-recipe'] || [];
        const top = data.recipe || [];
        
        setTodayRecipes(recommended); 
        setTopRecipes(top);

        // 로그인 상태일 때만 추가 데이터 가져오기
        if (isLoggedIn) {
          try {
            // 좋아요한 레시피 가져오기
            const likedResponse = await api.get('/api/user/liked-recipes');
            const recipes = likedResponse.data?.data || likedResponse.data || [];
            
            // 좋아요와 북마크 모두 같은 데이터로 설정
            setLikedRecipes(recipes);
            setBookmarkedRecipes(recipes); // 같은 데이터 사용
            
            console.log('좋아요한 레시피:', recipes.length, '개');
            
          } catch (likedError) {
            console.warn("좋아요한 레시피 API 오류:", likedError.response?.status);
            // 404 또는 500 에러는 무시 (API 미구현)
            if (likedError.response?.status === 404 || likedError.response?.status === 500) {
              setLikedRecipes([]);
              setBookmarkedRecipes([]);
            }
          }
        }

      } catch (error) {
        console.error("레시피 데이터를 불러오는 중 오류:", error);
        console.error("에러 상세:", error.response?.data);
        setError("레시피를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.");
      } finally {
        setLoading(false);
      }
    };

    fetchRecipes();
  }, [isLoggedIn]); // isLoggedIn이 변경될 때마다 다시 로드

  // 로딩 중일 때
  if (loading) {
    return (
      <div className="main-page-container">
        <Navigation />
        <div className="loading-container">
          <p>레시피를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  // 에러 발생 시
  if (error) {
    return (
      <div className="main-page-container">
        <Navigation />
        <div className="error-container">
          <p>{error}</p>
          <button onClick={() => window.location.reload()}>
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  // 데이터가 없을 때
  if (todayRecipes.length === 0 && topRecipes.length === 0) {
    return (
      <div className="main-page-container">
        <Navigation />
        <div className="no-data-container">
          <p>표시할 레시피가 없습니다.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="main-page-container">
      <Navigation />
      
      {/* 로그인 여부와 관계없이 항상 표시 */}
      <RecipeSection 
        title="금일의 레시피 추천" 
        recipes={todayRecipes} 
        sectionId="today-recommend"
      />
      
      <RecipeSection 
        title="인기 Top 10 레시피" 
        recipes={topRecipes}
        sectionId="top-10"
      />
      
      {/* 로그인 상태일 때만 표시 */}
      {isLoggedIn && likedRecipes.length > 0 && (
        <>
          <div className="footer-divider-wrapper">
            <div className="footer-divider"></div>
          </div>
          <RecipeSection 
            title="❤️ 내가 좋아요한 레시피" 
            recipes={likedRecipes}
            sectionId="liked-recipes"
          />
        </>
      )}
      
      <div className="footer-divider-wrapper">
        <div className="footer-divider"></div>
      </div>
    </div>
  );
}

export default MainPage;