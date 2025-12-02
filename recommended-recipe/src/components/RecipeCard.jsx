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

  return (
    <div className="recipe-card">
      <Link to={`/recipe/${recipeId}`}>
        <div className="image-container">
          <img 
            src={imageUrl} 
            alt={title} 
            className="recipe-image" 
          />
        </div>
        <div className="recipe-info">
          <h3 className="recipe-title">{title}</h3>
          <p className="recipe-description">{description}</p>
        </div>
      </Link>
    </div>
  );
}

export default RecipeCard;