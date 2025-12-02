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

  useEffect(() => {
    const fetchRecipe = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const response = await api.get(`/api/details/${recipeId}`);
        
        console.log('레시피 상세 응답:', response.data);
        
        const data = response.data.data;
        setRecipe(data.recipe);
        setIsLiked(data.isLiked || false);
        setLikeCount(data.recipe.rcmmCnt || 0);
        
      } catch (error) {
        console.error("레시피 상세 정보를 불러오는 데 실패했습니다:", error);
        setError("레시피를 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    if (recipeId) {
      fetchRecipe();
    }
  }, [recipeId]);

  const handleLike = async () => {
    try {
      const response = await api.post(`/api/details/likes`, null, {
        params: { recipe_id: recipeId }
      });
      
      // 성공적으로 처리됨
      setIsLiked(response.data.isLiked);
      setLikeCount(response.data.likeCount);
      
    } catch (error) {
      console.error("좋아요 실패:", error);
      
      // 401 에러 (로그인 필요)
      if (error.response?.status === 401) {
        const confirmLogin = window.confirm('로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?');
        if (confirmLogin) {
          navigate('/login');
        }
      } else {
        alert('좋아요 처리에 실패했습니다.');
      }
    }
  };
  
  const handleComplete = async () => {
    try {
      const response = await api.post(`/api/details/completion`, null, {
        params: { recipe_id: recipeId }
      });
      
      alert('레시피 사용 완료!');
      
    } catch (error) {
      console.error("사용 완료 실패:", error);
      
      // 401 에러 (로그인 필요)
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
        <p>레시피를 불러오는 중...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="detail-page-container">
        <p>{error}</p>
        <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className="detail-page-container">
        <p>해당 레시피를 찾을 수 없습니다.</p>
        <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
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