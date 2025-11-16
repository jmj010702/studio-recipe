// src/components/RecipeCard.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './RecipeCard.css';

function RecipeCard({ recipe }) {
  // DTO와 Entity 모두 지원
  const recipeId = recipe.recipeId || recipe.rcpSno;
  const title = recipe.title || recipe.rcpTtl;
  const imageUrl = recipe.imageUrl || recipe.rcpImgUrl || '/default-recipe-image.jpg';
  const description = recipe.ckgNm || recipe.ckgDodfNm || '';
  const viewCount = recipe.inqCnt || 0;
  const likeCount = recipe.rcmmCnt || 0;

  return (
    <div className="recipe-card">
      <Link to={`/details/${recipeId}`}>  {/* ✅ 올바른 경로로 수정 */}
        <div className="image-container">
          <img 
            src={imageUrl} 
            alt={title} 
            className="recipe-image" 
            onError={(e) => {
              e.target.src = '/default-recipe-image.jpg';  // ✅ 이미지 로드 실패 시 기본 이미지
            }}
          />
        </div>
        <div className="recipe-info">
          <h3 className="recipe-title">{title}</h3>
          <p className="recipe-description">{description}</p>
          
          {/* ✅ 조회수와 좋아요 수 표시 */}
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