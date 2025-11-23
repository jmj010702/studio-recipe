// src/components/RecipeCard.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './RecipeCard.css';

function RecipeCard({ recipe }) {
  // ✅ 필드명 호환성: 서버에서 title/imageUrl로 올 수도, rcpTtl/rcpImgUrl로 올 수도 있음
  const recipeId = recipe.recipeId || recipe.rcpSno;
  const title = recipe.title || recipe.rcpTtl || '제목 없음';
  const imageUrl = recipe.imageUrl || recipe.rcpImgUrl || '/default-recipe-image.jpg';
  const description = recipe.categoryName || recipe.ckgNm || recipe.ckgDodfNm || '';
  const viewCount = recipe.viewCount || recipe.inqCnt || 0;
  const likeCount = recipe.likeCount || recipe.rcmmCnt || 0;

  return (
    <div className="recipe-card">
      <Link to={`/details/${recipeId}`}>
        <div className="image-container">
          <img 
            src={imageUrl} 
            alt={title} 
            className="recipe-image" 
            onError={(e) => {
              e.target.src = '/default-recipe-image.jpg';
            }}
          />
        </div>
        <div className="recipe-info">
          <h3 className="recipe-title">{title}</h3>
          <p className="recipe-description">{description}</p>
          
          <div className="recipe-stats">
            <span className="stat-item">
              👁️ {viewCount.toLocaleString()}
            </span>
            <span className="stat-item">
              ❤️ {likeCount.toLocaleString()}
            </span>
          </div>
        </div>
      </Link>
    </div>
  );
}

export default RecipeCard;