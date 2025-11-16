// src/page/RecipeDetailPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './RecipeDetailPage.css'; 

function RecipeDetailPage() {
  const { recipeId } = useParams(); 
  const [recipe, setRecipe] = useState(null);
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 🔍 디버깅: useParams 확인
  useEffect(() => {
    console.log('🎯 현재 URL:', window.location.pathname);
    console.log('🎯 useParams 결과:', { recipeId });
    console.log('🎯 recipeId 타입:', typeof recipeId);
  }, [recipeId]);

  useEffect(() => {
    const fetchRecipe = async () => {
      setLoading(true);
      setError(null);
      
      try {
        console.log('🔍 레시피 조회 시작 - ID:', recipeId);
        console.log('🔍 recipeId 존재 여부:', !!recipeId);
        const response = await api.get(`/api/details/${recipeId}`);
        
        console.log('✅ 레시피 상세 응답:', response.data);
        
        // 응답 구조에 따라 데이터 추출 (여러 케이스 처리)
        let recipeData, isLikedData;
        
        if (response.data.data) {
          // 케이스 A: { data: { recipe: {...}, isLiked: true } }
          recipeData = response.data.data.recipe;
          isLikedData = response.data.data.isLiked;
        } else if (response.data.recipe) {
          // 케이스 B: { recipe: {...}, isLiked: true }
          recipeData = response.data.recipe;
          isLikedData = response.data.isLiked;
        } else {
          // 케이스 C: 직접 레시피 데이터
          recipeData = response.data;
          isLikedData = false;
        }
        
        console.log('📦 추출된 레시피 데이터:', recipeData);
        
        if (!recipeData) {
          throw new Error('레시피 데이터가 없습니다.');
        }
        
        setRecipe(recipeData);
        setIsLiked(isLikedData || false);
        setLikeCount(recipeData.rcmmCnt || 0);
        
      } catch (error) {
        console.error("❌ 레시피 상세 정보를 불러오는 데 실패:", error);
        
        if (error.response) {
          console.error('응답 상태:', error.response.status);
          console.error('응답 데이터:', error.response.data);
          
          if (error.response.status === 404) {
            setError("해당 레시피를 찾을 수 없습니다.");
          } else if (error.response.status === 401) {
            setError("로그인이 필요합니다.");
          } else {
            setError(`레시피를 불러오는데 실패했습니다. (${error.response.status})`);
          }
        } else {
          setError("네트워크 오류가 발생했습니다.");
        }
      } finally {
        setLoading(false);
      }
    };

    if (recipeId) {
      fetchRecipe();
    } else {
      setError('레시피 ID가 없습니다.');
      setLoading(false);
    }
  }, [recipeId]);

  const handleLike = async () => {
    try {
      console.log('❤️ 좋아요 요청 - recipeId:', recipeId);
      
      const response = await api.post(`/api/details/likes`, null, {
        params: { recipe_id: recipeId }
      });
      
      console.log('좋아요 응답:', response.data);
      
      setIsLiked(response.data.isLiked);
      setLikeCount(response.data.likeCount);
      
    } catch (error) {
      console.error("❌ 좋아요 실패:", error);
      
      if (error.response?.status === 401) {
        const confirmLogin = window.confirm('로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?');
        if (confirmLogin) {
          navigate('/login');
        }
      } else {
        alert('로그인후 사용해주시기 바랍니다.');
      }
    }
  };
  
  const handleComplete = async () => {
    try {
      console.log('✅ 사용 완료 요청 - recipeId:', recipeId);
      
      const response = await api.post(`/api/details/completion`, null, {
        params: { recipe_id: recipeId }
      });
      
      console.log('사용 완료 응답:', response.data);
      alert('레시피 사용 완료!');
      
    } catch (error) {
      console.error("❌ 사용 완료 실패:", error);
      
      if (error.response?.status === 401) {
        const confirmLogin = window.confirm('로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?');
        if (confirmLogin) {
          navigate('/login');
        }
      } else {
        alert('사용 완료 처리에 실패했습니다.');
      }
    }
  };

  if (loading) {
    return (
      <div className="detail-page-container">
        <div className="loading-spinner">
          <p>레시피를 불러오는 중...</p>
          <p className="loading-id">ID: {recipeId}</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="detail-page-container">
        <div className="error-message">
          <h2>오류 발생</h2>
          <p>{error}</p>
          <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
        </div>
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className="detail-page-container">
        <div className="error-message">
          <p>해당 레시피를 찾을 수 없습니다.</p>
          <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
        </div>
      </div>
    );
  }

  const title = recipe.rcpTtl || '제목 없음';
  const imageUrl = recipe.rcpImgUrl || '/default-recipe-image.jpg';
  const cookingName = recipe.ckgNm || '';
  const difficulty = recipe.ckgDodfNm || '';
  const servings = recipe.ckgInbunNm || '';
  const cookingTime = recipe.ckgTimeNm || '';
  const ingredients = recipe.ckgMtrlCn || '재료 정보가 없습니다.';
  const method = recipe.ckgMthActoNm || '';
  const viewCount = recipe.inqCnt || 0;

  const ingredientList = ingredients.split(/[\n,]/).filter(item => item.trim());

  return (
    <div className="detail-page-container">
      <div className="recipe-header">
        <h1>{title}</h1>
        {cookingName && <p className="cooking-name">{cookingName}</p>}
        
        <div className="recipe-meta">
          {difficulty && <span className="meta-item">난이도: {difficulty}</span>}
          {servings && <span className="meta-item">인분: {servings}</span>}
          {cookingTime && <span className="meta-item">조리시간: {cookingTime}</span>}
        </div>
      </div>

      <img src={imageUrl} alt={title} className="recipe-main-image" />
      
      <div className="recipe-actions">
        <button 
          onClick={handleLike} 
          className={`like-btn ${isLiked ? 'liked' : ''}`}
        >
          {isLiked ? '❤️' : '🤍'} 좋아요 {likeCount}
        </button>
        <button onClick={handleComplete} className="complete-btn">
          ✅ 사용 완료
        </button>
        <span className="view-count">조회수: {viewCount}</span>
      </div>

      <div className="recipe-content-box">
        <h2>재료</h2>
        <ul className="ingredient-list">
          {ingredientList.length > 0 ? (
            ingredientList.map((item, index) => (
              <li key={index}>{item.trim()}</li>
            ))
          ) : (
            <li>재료 정보가 없습니다.</li>
          )}
        </ul>
      </div>

      {method && (
        <div className="recipe-content-box">
          <h2>조리 방법</h2>
          <p className="cooking-method">{method}</p>
        </div>
      )}

      <button onClick={() => navigate(-1)} className="back-btn">
        ← 뒤로 가기
      </button>
    </div>
  );
}

export default RecipeDetailPage;