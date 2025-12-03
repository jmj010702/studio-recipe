import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import RecipeCard from '../components/RecipeCard';
import './SearchResultPage.css';

function SearchResultPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const query = searchParams.get('q') || ''; // 검색어 (콤마로 구분된 재료 또는 제목)
  const searchType = searchParams.get('type') || 'title';

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // ▼▼▼ [추가] 재료 검색용 상태 ▼▼▼
  const [ingredientInput, setIngredientInput] = useState('');
  const [ingredients, setIngredients] = useState([]);

  // 초기 로드 시 URL 쿼리를 태그로 변환
  useEffect(() => {
    if (searchType === 'ingredients' && query) {
      setIngredients(query.split(',').map(s => s.trim()).filter(Boolean));
    }
  }, [query, searchType]);

  // 재료 추가 핸들러
  const handleAddIngredient = (e) => {
    if (e.key === 'Enter' && ingredientInput.trim()) {
      e.preventDefault();
      const newIngredient = ingredientInput.trim();
      
      if (!ingredients.includes(newIngredient)) {
        const newIngredients = [...ingredients, newIngredient];
        updateSearch(newIngredients);
      }
      setIngredientInput('');
    }
  };

  // 재료 삭제 핸들러
  const handleRemoveIngredient = (target) => {
    const newIngredients = ingredients.filter(ing => ing !== target);
    updateSearch(newIngredients);
  };

  // 검색 실행 (URL 업데이트 -> useEffect가 감지해서 fetch)
  const updateSearch = (newIngredients) => {
    setIngredients(newIngredients);
    const newQuery = newIngredients.join(',');
    
    if (newIngredients.length === 0) {
      // 재료가 다 지워지면 검색 결과 초기화하거나 전체 목록? (여기서는 유지)
      navigate(`/search?q=&type=ingredients`);
    } else {
      navigate(`/search?q=${encodeURIComponent(newQuery)}&type=ingredients`);
    }
    setCurrentPage(0); // 페이지 초기화
  };
  // ▲▲▲ [추가 끝] ▲▲▲

  useEffect(() => {
    const fetchResults = async () => {
      if (!query && searchType === 'title') return; // 제목 검색인데 검색어 없으면 중단
      if (searchType === 'ingredients' && !query) { // 재료 검색인데 재료 없으면 결과 초기화
         setResults([]);
         return;
      }

      setLoading(true);
      setError(null);

      try {
        let response;
        // ✅ 백엔드 API 엔드포인트 확인 필요 (기존 코드 기반)
        // /api/search/title, /api/search/ingredients
        const endpoint = searchType === 'title' ? '/api/search/title' : '/api/search/ingredients';
        
        response = await axios.get(endpoint, {
          params: { q: query, page: currentPage, size: 16 }
        });

        const pageData = response.data;
        setResults(pageData.content || []);
        setTotalPages(pageData.totalPages || 0);
      } catch (err) {
        console.error('검색 에러:', err);
        setError('검색 중 오류가 발생했습니다.');
        setResults([]);
        setTotalPages(0);
      } finally {
        setLoading(false);
      }
    };

    fetchResults();
  }, [query, searchType, currentPage]);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // ... renderPagination 함수는 기존과 동일 ...
  const renderPagination = () => {
    if (totalPages <= 1) return null;
    const pageNumbers = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }
    for (let i = startPage; i <= endPage; i++) pageNumbers.push(i);

    return (
      <div className="pagination">
        <button onClick={() => handlePageChange(0)} disabled={currentPage === 0} className="pagination-btn">처음</button>
        <button onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 0} className="pagination-btn">이전</button>
        {pageNumbers.map((number) => (
          <button key={number} onClick={() => handlePageChange(number)} className={`pagination-btn ${currentPage === number ? 'active' : ''}`}>{number + 1}</button>
        ))}
        <button onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages - 1} className="pagination-btn">다음</button>
        <button onClick={() => handlePageChange(totalPages - 1)} disabled={currentPage === totalPages - 1} className="pagination-btn">마지막</button>
      </div>
    );
  };

  return (
    <div className="search-result-page">
      <div className="search-header">
        <h2>
          {searchType === 'title' ? '레시피명' : '재료'} 검색 결과
          {searchType === 'title' && `: "${query}"`}
        </h2>
        
        {/* ▼▼▼ [추가] 재료 검색일 때만 태그 입력창 표시 ▼▼▼ */}
        {searchType === 'ingredients' && (
          <div className="ingredient-search-area">
            <div className="ingredient-tags">
              {ingredients.map((ing, idx) => (
                <span key={idx} className="ingredient-tag">
                  {ing}
                  <button onClick={() => handleRemoveIngredient(ing)}>×</button>
                </span>
              ))}
            </div>
            <input
              type="text"
              className="ingredient-input"
              placeholder="재료를 추가하고 엔터 (예: 계란)"
              value={ingredientInput}
              onChange={(e) => setIngredientInput(e.target.value)}
              onKeyDown={handleAddIngredient}
            />
          </div>
        )}
        {/* ▲▲▲ [추가 끝] ▲▲▲ */}

        <p className="result-count">총 {results.length}개의 레시피</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">검색 중...</div>
      ) : results.length === 0 ? (
        <div className="no-results">
          <p>검색 결과가 없습니다.</p>
          {searchType === 'title' && (
             <button onClick={() => navigate('/')} className="back-btn">메인으로 돌아가기</button>
          )}
        </div>
      ) : (
        <>
          <div className="recipe-grid">
            {results.map((recipe) => (
              <RecipeCard key={recipe.recipeId || recipe.rcpSno} recipe={recipe} />
            ))}
          </div>
          {renderPagination()}
        </>
      )}
    </div>
  );
}

export default SearchResultPage;