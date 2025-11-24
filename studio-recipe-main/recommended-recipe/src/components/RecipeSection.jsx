// src/components/RecipeSection.jsx
import React from 'react';
import RecipeCard from './RecipeCard.jsx';
import SortFilters from './SortFilters.jsx';
import './RecipeSection.css';

function RecipeSection({ title, recipes, sectionId }) {

  const handleSortChange = (sortType) => {
    console.log(`${title} 섹션 정렬 변경: ${sortType}`);
  };

  return (
    // 1. (배경) 100% 너비 (배경은 MainPage.css가 담당)
    <section className="recipe-section" id={sectionId || ''}>
      {/* 2. (콘텐츠) 중앙 정렬되는 ★흰색 박스★ */}
      <div className="recipe-section-content">
        <div className="section-header">
          <h2 className="section-title">{title}</h2>
          
          {/* ▼▼▼▼▼ (수정) 필터와 더보기 버튼을 묶는 래퍼 ▼▼▼▼▼ */}
          <div className="header-right">
            <SortFilters onSortChange={handleSortChange} />
            <button className="see-more-btn">더보기</button>
          </div>
          {/* ▲▲▲▲▲ (수정) ▲▲▲▲▲ */}

        </div>
        <div className="recipe-list">
          {!recipes || recipes.length === 0 ? (
            <p className="no-recipes">표시할 레시피가 없습니다.</p>
          ) : (
            recipes.map(recipe => (
              <RecipeCard key={recipe.id} recipe={recipe} />
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default RecipeSection;