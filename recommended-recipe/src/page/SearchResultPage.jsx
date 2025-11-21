import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import RecipeCard from '../components/RecipeCard';
import './SearchResultPage.css';

function SearchResultPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const query = searchParams.get('q');
  const searchType = searchParams.get('type') || 'title';

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchResults = async () => {
      if (!query) return;

      setLoading(true);
      setError(null);

      try {
        let response;
        if (searchType === 'title') {
          response = await axios.get(`/api/search/title`, {
            params: { q: query, page: currentPage, size: 16 }
          });
        } else {
          response = await axios.get(`/api/search/ingredients`, {
            params: { q: query, page: currentPage, size: 16 }
          });
        }

        // Spring Page 객체 구조 처리
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

  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pageNumbers = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    return (
      <div className="pagination">
        <button
          onClick={() => handlePageChange(0)}
          disabled={currentPage === 0}
          className="pagination-btn"
        >
          처음
        </button>
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="pagination-btn"
        >
          이전
        </button>

        {pageNumbers.map((number) => (
          <button
            key={number}
            onClick={() => handlePageChange(number)}
            className={`pagination-btn ${currentPage === number ? 'active' : ''}`}
          >
            {number + 1}
          </button>
        ))}

        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages - 1}
          className="pagination-btn"
        >
          다음
        </button>
        <button
          onClick={() => handlePageChange(totalPages - 1)}
          disabled={currentPage === totalPages - 1}
          className="pagination-btn"
        >
          마지막
        </button>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="search-result-page">
        <div className="loading">검색 중...</div>
      </div>
    );
  }

  return (
    <div className="search-result-page">
      <div className="search-header">
        <h2>
          {searchType === 'title' ? '레시피명' : '재료'} 검색 결과: "{query}"
        </h2>
        <p className="result-count">
          총 {results.length}개의 레시피
        </p>
      </div>

      {error && <div className="error-message">{error}</div>}

      {!loading && results.length === 0 ? (
        <div className="no-results">
          <p>검색 결과가 없습니다.</p>
          <button onClick={() => navigate('/search')} className="back-btn">
            다시 검색하기
          </button>
        </div>
      ) : (
        <>
          <div className="recipe-grid">
            {results.map((recipe) => (
              <RecipeCard key={recipe.recipeId} recipe={recipe} />
            ))}
          </div>
          {renderPagination()}
        </>
      )}
    </div>
  );
}

export default SearchResultPage;