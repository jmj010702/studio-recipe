// src/components/RecipeCard.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './RecipeCard.css'; // RecipeCard CSS 임포트

function RecipeCard({ recipe }) {
  // API 응답에 따라 id 또는 recipeId 사용
  const recipeId = recipe.id || recipe.recipeId;

  return (
    <div className="recipe-card">
      {/* Link 태그로 카드 전체를 감싸서 클릭 가능하게 함 */}
      <Link to={`/recipe/${recipeId}`}>
        <div className="image-container">
          <img 
            src={recipe.imageUrl} 
            alt={recipe.title} 
            className="recipe-image" 
          />
        </div>
        <div className="recipe-info">
          <h3 className="recipe-title">{recipe.title}</h3>
          <p className="recipe-description">{recipe.description}</p>
        </div>
      </Link>
    </div>
  );
}

export default RecipeCard;