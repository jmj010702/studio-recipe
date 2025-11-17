// src/page/MainPage.jsx
import React, { useState, useEffect } from 'react';
import api from '../api/axios.js'; 
import Navigation from '../components/Navigation.jsx';
import RecipeSection from '../components/RecipeSection.jsx';
import './MainPage.css'; // MainPage 전용 CSS

// --- API 연동 전 임시 데이터 (디자인 확인용) ---
const mockTodayRecipes = [
  { id: 1, title: "명란마요초밥", description: "도시락에 빠질 수 없는 공유부초밥!", imageUrl: "https://via.placeholder.com/500x300.png?text=Mentaiko+Sushi" },
  { id: 2, title: "아시안 닭꼬치", description: "저녁 술안주로 딱!", imageUrl: "https://via.placeholder.com/500x300.png?text=Asian+Chicken+Skewer" }
];
const mockTopRecipes = [
  { id: 3, title: "불맛 잡채스테이크", description: "달콤짭짤한 소스의 매력!", imageUrl: "https://via.placeholder.com/500x300.png?text=Japchae+Steak" },
  { id: 4, title: "호텔 파스타", description: "집에서 즐기는 호텔급 맛", imageUrl: "https://via.placeholder.com/500x300.png?text=Hotel+Pasta" }
];
// --- 임시 데이터 끝 ---

function MainPage() {
  // API 연동 시: useState([])
  // 디자인 확인 시: useState(mockTodayRecipes)
  const [todayRecipes, setTodayRecipes] = useState(mockTodayRecipes);
  const [topRecipes, setTopRecipes] = useState(mockTopRecipes);
  
  // API 연동 로직 (현재는 주석 처리)
  /*
  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        const todayResponse = await api.get('/api/recipes/today-recommendation');
        setTodayRecipes(todayResponse.data);

        const topResponse = await api.get('/api/recipes/top-10');
        setTopRecipes(topResponse.data);
      } catch (error) {
        console.error("레시피 데이터를 불러오는 중 오류:", error);
      }
    };
    fetchRecipes();
  }, []);
  */

  // return 문은 단 하나의 태그로 시작해야 합니다.
  return (
    <div className="main-page-container">
      
      {/* 1. Navigation 컴포넌트 */}
      <Navigation />
      
      {/* 2. RecipeSection 컴포넌트 (금일의 레시피) */}
      <RecipeSection 
        title="금일의 레시피 추천" 
        recipes={todayRecipes} 
        sectionId="today-recommend"
      />
      
      {/* 3. 또 다른 RecipeSection (인기 Top 10) */}
      <RecipeSection 
        title="인기 Top 10 레시피" 
        recipes={topRecipes}
        sectionId="top-10"
      />
      
      {/* 4. 구분선 */}
      <div className="footer-divider-wrapper">
        <div className="footer-divider"></div>
      </div>

    </div> // <-- 모든 콘텐츠를 감싸는 최상위 div가 여기서 닫힙니다.
  );
}

export default MainPage;