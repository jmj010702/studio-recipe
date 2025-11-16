// src/components/RecipeSection.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import RecipeCard from './RecipeCard.jsx';
import SortFilters from './SortFilters.jsx';
import './RecipeSection.css';

function RecipeSection({ title, recipes, sectionId }) {
  const navigate = useNavigate();
  const [currentIndex, setCurrentIndex] = useState(0);
  const itemsPerPage = 4;

  // 키보드 이벤트 핸들러 삭제됨

  const handleNext = () => {
    if (!recipes || recipes.length === 0) return;
    
    setCurrentIndex((prev) => {
      const nextIndex = prev + itemsPerPage;
      return nextIndex >= recipes.length ? 0 : nextIndex;
    });
  };

  const handlePrev = () => {
    if (!recipes || recipes.length === 0) return;
    
    setCurrentIndex((prev) => {
      const prevIndex = prev - itemsPerPage;
      if (prevIndex < 0) {
        const lastPageIndex = Math.floor((recipes.length - 1) / itemsPerPage) * itemsPerPage;
        return lastPageIndex;
      }
      return prevIndex;
    });
  };

  const handleSortChange = (sortType) => {
    console.log(`${title} 섹션 정렬 변경: ${sortType}`);
  };

  const handleSeeMore = () => {
    if (sectionId === 'today-recommend') {
      navigate('/recipes/recommended');
    } else if (sectionId === 'top-10') {
      navigate('/recipes/popular');
    } else if (sectionId === 'liked-recipes') {
      navigate('/recipes/liked');
    } else {
      navigate('/recipes/all');
    }
  };

  const visibleRecipes = recipes?.slice(currentIndex, currentIndex + itemsPerPage) || [];
  const totalPages = recipes ? Math.ceil(recipes.length / itemsPerPage) : 0;
  const currentPage = recipes && recipes.length > 0 ? Math.floor(currentIndex / itemsPerPage) + 1 : 0;

  return (
    <section className="recipe-section" id={sectionId || ''}>
      <div className="recipe-section-content">
        <div className="section-header">
          <h2 className="section-title">{title}</h2>
          
          <div className="header-right">
            <SortFilters onSortChange={handleSortChange} />
            
            {recipes && recipes.length > itemsPerPage && (
              <div className="carousel-navigation">
                <button 
                  onClick={handlePrev} 
                  className="carousel-btn"
                  aria-label="이전"
                >
                  &lt;
                </button>
                <span className="page-indicator">
                  {currentPage} / {totalPages}
                </span>
                <button 
                  onClick={handleNext} 
                  className="carousel-btn"
                  aria-label="다음"
                >
                  &gt;
                </button>
              </div>
            )}
            
            <button className="see-more-btn" onClick={handleSeeMore}>
              더보기
            </button>
          </div>
        </div>

        <div className="recipe-list">
          {!recipes || recipes.length === 0 ? (
            <p className="no-recipes">표시할 레시피가 없습니다.</p>
          ) : (
            visibleRecipes.map(recipe => (
              <RecipeCard 
                key={recipe.recipeId || recipe.rcpSno} 
                recipe={recipe} 
              />
            ))
          )}
        </div>

        {recipes && recipes.length > itemsPerPage && (
          <div className="pagination-dots">
            {Array.from({ length: totalPages }).map((_, idx) => (
              <button
                key={idx}
                className={`dot ${idx === currentPage - 1 ? 'active' : ''}`}
                onClick={() => setCurrentIndex(idx * itemsPerPage)}
                aria-label={`${idx + 1}페이지로 이동`}
              />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

export default RecipeSection;