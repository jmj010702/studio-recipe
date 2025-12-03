// src/components/RecipeSection.jsx
import React from 'react';
import RecipeCard from './RecipeCard.jsx';
import SortFilters from './SortFilters.jsx';
import './RecipeSection.css';

function RecipeSection({ title, recipes, sectionId }) {
  // ✅ recipes가 배열이 아닐 때 에러 방지
  const safeRecipes = Array.isArray(recipes) ? recipes : [];

  const handleSortChange = (sortType) => {
    console.log(`${title} 섹션 정렬 변경: ${sortType}`);
  };

  return (
    <section className="recipe-section" id={sectionId || ''}>
      <div className="recipe-section-content">
        <div className="section-header">
          <h2 className="section-title">{title}</h2>

          <div className="header-right">
            <SortFilters onSortChange={handleSortChange} />
            <button className="see-more-btn">더보기</button>
          </div>
        </div>

        <div className="recipe-list">
          {safeRecipes.length === 0 ? (
            <p className="no-recipes">표시할 레시피가 없습니다.</p>
          ) : (
            safeRecipes.map((recipe) => (
              <RecipeCard key={recipe.id || recipe.recipeId} recipe={recipe} />
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default RecipeSection;
