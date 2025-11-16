// src/page/RecipeListPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navigation from '../components/Navigation.jsx';
import RecipeCard from '../components/RecipeCard.jsx';
import SortFilters from '../components/SortFilters.jsx';
import api from '../api/axios.js';
import './RecipeListPage.css';

function RecipeListPage() {
  const { type } = useParams(); // recommended, popular, liked, all
  const navigate = useNavigate();
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortType, setSortType] = useState('latest');

  // 페이지 제목 결정
  const getPageTitle = () => {
    switch(type) {
      case 'recommended': return '추천 레시피';
      case 'popular': return '인기 레시피';
      case 'liked': return '좋아요한 레시피';
      default: return '전체 레시피';
    }
  };

  useEffect(() => {
    const fetchRecipes = async () => {
      try {
        setLoading(true);
        setError(null);

        let response;
        switch(type) {
          case 'recommended':
            // 추천 레시피 전체 조회
            response = await api.get('/api/recipes/recommended');
            break;
          case 'popular':
            // 인기 레시피 전체 조회
            response = await api.get('/api/recipes/popular');
            break;
          case 'liked':
            // 좋아요한 레시피 전체 조회
            response = await api.get('/api/user/liked-recipes');
            break;
          default:
            // 전체 레시피 조회
            response = await api.get('/api/recipes/all');
        }

        setRecipes(response.data?.data || response.data || []);
      } catch (error) {
        console.error('레시피 목록 조회 실패:', error);
        setError('레시피를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchRecipes();
  }, [type]);

  const handleSortChange = (newSortType) => {
    setSortType(newSortType);
    // TODO: 정렬 로직 구현
    console.log('정렬 타입:', newSortType);
  };

  if (loading) {
    return (
      <div className="recipe-list-page">
        <Navigation />
        <div className="loading-container">
          <p>레시피를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="recipe-list-page">
        <Navigation />
        <div className="error-container">
          <p>{error}</p>
          <button onClick={() => navigate('/')}>메인으로 돌아가기</button>
        </div>
      </div>
    );
  }

  return (
    <div className="recipe-list-page">
      <Navigation />
      
      <div className="recipe-list-container">
        <div className="list-header">
          <h1 className="list-title">{getPageTitle()}</h1>
          <div className="list-controls">
            <SortFilters onSortChange={handleSortChange} />
            <span className="recipe-count">총 {recipes.length}개</span>
          </div>
        </div>

        {recipes.length === 0 ? (
          <div className="no-recipes">
            <p>표시할 레시피가 없습니다.</p>
            <button onClick={() => navigate('/')}>메인으로 돌아가기</button>
          </div>
        ) : (
          <div className="recipe-grid">
            {recipes.map(recipe => (
              <RecipeCard 
                key={recipe.recipeId || recipe.rcpSno} 
                recipe={recipe} 
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default RecipeListPage;