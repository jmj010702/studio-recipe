import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import RecipeCard from './RecipeCard.jsx';
import SortFilters from './SortFilters.jsx';
import './RecipeSection.css';

<<<<<<< HEAD
function RecipeSection({ title, recipes, sectionId }) {
  // ✅ recipes가 배열이 아닐 때 에러 방지
  const safeRecipes = Array.isArray(recipes) ? recipes : [];
=======
function RecipeSection({ title, recipes, sectionId, sortType, onSortChange }) {
  const navigate = useNavigate();
  const [currentIndex, setCurrentIndex] = useState(0);
  const itemsPerPage = 4;
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a

  // recipes나 sortType이 변경되면 페이지를 첫 번째로 리셋
  useEffect(() => {
    console.log(`🔄 페이지 리셋 - ${title}`);
    setCurrentIndex(0);
  }, [recipes, sortType, title]);

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

  const handleSortChange = (sortTypeValue) => {
    console.log(`📢 RecipeSection에서 정렬 호출: ${sortTypeValue}`);
    console.log('onSortChange 함수 존재?', !!onSortChange);
    console.log('onSortChange:', onSortChange);
    if (onSortChange) {
      console.log('✅ onSortChange 실행!');
      onSortChange(sortTypeValue);
    } else {
      console.log('❌ onSortChange가 없습니다!');
    }
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
<<<<<<< HEAD

=======
          
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
          <div className="header-right">
            {/* sortType과 onSortChange가 있을 때만 정렬 필터 표시 */}
            {sortType !== undefined && onSortChange && (
              <SortFilters 
                onSortChange={handleSortChange}
                currentSort={sortType}
              />
            )}
            
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
          {safeRecipes.length === 0 ? (
            <p className="no-recipes">표시할 레시피가 없습니다.</p>
          ) : (
<<<<<<< HEAD
            safeRecipes.map((recipe) => (
              <RecipeCard key={recipe.id || recipe.recipeId} recipe={recipe} />
=======
            visibleRecipes.map(recipe => (
              <RecipeCard 
                key={recipe.recipeId || recipe.rcpSno} 
                recipe={recipe} 
              />
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
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
