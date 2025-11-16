// src/page/SearchResultPage.jsx
import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../api/axios.js';
import RecipeSection from '../components/RecipeSection.jsx';
import './SearchResultPage.css';

function SearchResultPage() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q');
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSearchResults = async () => {
      if (!query) return;
      
      try {
        setLoading(true);
        setError(null);
        
        const response = await api.get(`/api/recipes/search`, {
          params: { keyword: query }
        });
        
        setRecipes(response.data.data || []);
      } catch (error) {
        console.error('검색 실패:', error);
        setError('검색 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchSearchResults();
  }, [query]);

  if (loading) {
    return <div className="search-page">검색 중...</div>;
  }

  if (error) {
    return <div className="search-page error">{error}</div>;
  }

  return (
    <div className="search-page">
      <h1>"{query}" 검색 결과</h1>
      <p className="result-count">총 {recipes.length}개</p>
      
      {recipes.length === 0 ? (
        <p className="no-results">검색 결과가 없습니다.</p>
      ) : (
        <RecipeSection 
          title="" 
          recipes={recipes}
          sectionId="search-results"
        />
      )}
    </div>
  );
}

export default SearchResultPage;