<<<<<<< HEAD
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
=======
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
  
  // 정렬 상태
  const [sortType, setSortType] = useState('recommend');
  
  // 날짜 기반 시드 생성 함수
  const getDailySeed = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const day = today.getDate();
    return year * 10000 + month * 100 + day;
  };

  // 5일 주기 계산 함수
  const get5DayCycle = () => {
    const today = new Date();
    const daysSinceEpoch = Math.floor(today.getTime() / (1000 * 60 * 60 * 24));
    return daysSinceEpoch % 5;
  };

  // 시드 기반 셔플 함수 (Fisher-Yates)
  const shuffleWithSeed = (array, seed) => {
    const shuffled = [...array];
    let currentSeed = seed;
    
    const random = () => {
      currentSeed = (currentSeed * 1103515245 + 12345) & 0x7fffffff;
      return currentSeed / 0x7fffffff;
    };
    
    for (let i = shuffled.length - 1; i > 0; i--) {
      const j = Math.floor(random() * (i + 1));
      [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    
    return shuffled;
  };

  // ✅ 레시피 정렬 함수 (백엔드 필드명 사용)
  const sortRecipes = (recipes, type) => {
    if (!recipes || recipes.length === 0) return recipes;
    
    const sorted = [...recipes];
    
    switch(type) {
      case 'views': // 조회수
        console.log('📊 조회수(inqCnt)로 정렬 중...');
        return sorted.sort((a, b) => (b.inqCnt || 0) - (a.inqCnt || 0));
      
      case 'likes': // 추천수
        console.log('❤️ 추천수(rcmmCnt)로 정렬 중...');
        return sorted.sort((a, b) => (b.rcmmCnt || 0) - (a.rcmmCnt || 0));
      
      case 'latest': // 최신순
        console.log('🆕 최신순(firstRegDt)으로 정렬 중...');
        return sorted.sort((a, b) => {
          const dateA = new Date(a.firstRegDt || 0);
          const dateB = new Date(b.firstRegDt || 0);
          return dateB - dateA;
        });
      
      case 'recommend': // 추천순 (기본)
      default:
        console.log('⭐ 추천순 (기본 - 셔플 상태 유지)');
        return recipes;
    }
  };

  // 5일 동안 겹치지 않는 레시피 선택 함수
  const get5DayUniqueRecipes = (recipes, recipesPerDay = 10) => {
    if (recipes.length === 0) {
      return [];
    }

    const uniqueRecipes = recipes.reduce((acc, recipe) => {
      // ✅ rcpSno를 ID로 사용
      const id = recipe.rcpSno;
      const exists = acc.find(r => r.rcpSno === id);
      if (!exists) {
        acc.push(recipe);
      }
      return acc;
    }, []);

    const dailySeed = getDailySeed();
    const dayInCycle = get5DayCycle();
    
    const shuffled = shuffleWithSeed(uniqueRecipes, dailySeed);
    
    const neededRecipes = recipesPerDay * 5;
    if (uniqueRecipes.length >= neededRecipes) {
      const startIndex = dayInCycle * recipesPerDay;
      const endIndex = startIndex + recipesPerDay;
      return shuffled.slice(startIndex, endIndex);
    }
    
    const offset = (dayInCycle * recipesPerDay) % uniqueRecipes.length;
    const selected = [];
    
    for (let i = 0; i < recipesPerDay && selected.length < uniqueRecipes.length; i++) {
      const index = (offset + i) % uniqueRecipes.length;
      const recipe = shuffled[index];
      
      // ✅ rcpSno를 ID로 사용
      const recipeId = recipe.rcpSno;
      const isDuplicate = selected.find(r => r.rcpSno === recipeId);
      
      if (!isDuplicate) {
        selected.push(recipe);
      }
    }
    
    return selected;
  };

  // ⭐ 로그인 상태 확인 useEffect 추가
  useEffect(() => {
    const checkLoginStatus = () => {
      const loggedIn = isAuthenticated();
      console.log('🔐 로그인 상태 확인:', loggedIn);
      setIsLoggedIn(loggedIn);
    };

    checkLoginStatus();
  }, []);

  // 레시피 데이터 로드
  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        setLoading(true);
        setError(null);
        
        console.log('🔍 API 호출 시작: /api/mainPages');
        const response = await api.get('/api/mainPages');
        console.log('✅ API 응답 전체:', response.data);
        
        const data = response.data?.data || {};
        const recommended = data['recommended-recipe'] || [];
        const top = data.recipe || [];
        
        console.log('📦 recommended 데이터 개수:', recommended.length);
        console.log('📦 top 데이터 개수:', top.length);
        
        const todayUniqueRecipes = get5DayUniqueRecipes(recommended, 10);
        
        // 🔍 데이터 구조 상세 확인 (이미지 URL 집중 체크)
        if (todayUniqueRecipes.length > 0) {
          console.log('=== 첫 번째 레시피 데이터 상세 확인 ===');
          const firstRecipe = todayUniqueRecipes[0];
          console.log('전체 객체:', firstRecipe);
          console.log('rcpSno (ID):', firstRecipe.rcpSno);
          console.log('rcpTtl (제목):', firstRecipe.rcpTtl);
          console.log('rcpImgUrl (이미지 URL):', firstRecipe.rcpImgUrl);
          console.log('imageUrl 필드:', firstRecipe.imageUrl);
          console.log('모든 키:', Object.keys(firstRecipe));
        }

        setTodayRecipes(todayUniqueRecipes); 
        setTopRecipes(top);

        // ⭐ 로그인 상태 체크 후 좋아요한 레시피 불러오기
        if (isLoggedIn) {
          try {
            console.log('🔍 좋아요한 레시피 조회 시작');
            const likedResponse = await api.get('/api/user/liked-recipes');
            const recipes = likedResponse.data?.data || likedResponse.data || [];
            
            console.log('❤️ 좋아요한 레시피 개수:', recipes.length);
            
            // ⭐ 좋아요한 레시피 이미지 URL 확인
            if (recipes.length > 0) {
              console.log('=== 좋아요한 레시피 데이터 상세 확인 ===');
              recipes.forEach((recipe, index) => {
                console.log(`레시피 ${index + 1}:`, {
                  rcpSno: recipe.rcpSno,
                  rcpTtl: recipe.rcpTtl,
                  rcpImgUrl: recipe.rcpImgUrl,
                });
              });
            }
            
            setLikedRecipes(recipes);
            setBookmarkedRecipes(recipes);
            
          } catch (likedError) {
            console.error('❌ 좋아요한 레시피 조회 실패:', likedError);
            if (likedError.response?.status === 404 || likedError.response?.status === 500) {
              setLikedRecipes([]);
              setBookmarkedRecipes([]);
            }
          }
        } else {
          console.log('❌ 로그인 상태가 아니므로 좋아요한 레시피를 불러오지 않습니다.');
        }

      } catch (error) {
        console.error("❌ 레시피 데이터를 불러오는 중 오류:", error);
        console.error("에러 상세:", error.response?.data);
        setError("레시피를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.");
      } finally {
        setLoading(false);
      }
    };

    fetchRecipes();
  }, [isLoggedIn]);

  // 정렬 핸들러
  const handleSortChange = (type) => {
    console.log('🔀 MainPage에서 정렬 변경:', type);
    setSortType(type);
  };

  // 정렬된 레시피 계산
  const sortedTodayRecipes = sortRecipes(todayRecipes, sortType);

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
      
      <RecipeSection 
        title="금일의 레시피 추천" 
        recipes={sortedTodayRecipes} 
        sectionId="today-recommend"
        sortType={sortType}
        onSortChange={handleSortChange}
      />
      
      <RecipeSection 
        title="인기 Top 10 레시피" 
        recipes={topRecipes}
        sectionId="top-10"
      />
      
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
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
      </div>
    </div>
  );
}

export default MyPage;
