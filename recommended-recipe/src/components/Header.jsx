import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { FaUserCircle, FaEdit, FaBell, FaSearch } from 'react-icons/fa';
import { VscAccount, VscSignOut } from 'react-icons/vsc';
import api from '../api/axios'; 
import './Header.css'; 

function Header() {
  const [searchTerm, setSearchTerm] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [searchResults, setSearchResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false); 
  const [isLoggedIn, setIsLoggedIn] = useState(false); 

  const navigate = useNavigate();
  const location = useLocation();
  const dropdownRef = useRef(null);
  const debounceTimerRef = useRef(null);
  const searchWrapperRef = useRef(null); 

  // 로그인 상태 관리
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
  }, [location]);

  // ✅ [수정 1] 검색 제출 (엔터 or 돋보기 클릭)
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const term = searchTerm.trim();
    if (term) {
      // 콤마(,)가 있으면 '재료 검색'으로, 없으면 '제목 검색'으로 이동
      const searchType = term.includes(',') ? 'ingredients' : 'title';
      
      console.log(`🔍 검색 실행: "${term}" (타입: ${searchType})`);
      
      navigate(`/search?q=${encodeURIComponent(term)}&type=${searchType}`);
      setSearchResults([]);
      setSearchTerm('');
    }
  };

  // ✅ [수정 2] 자동완성 검색 (스마트 감지)
  useEffect(() => {
    if (debounceTimerRef.current) {
      clearTimeout(debounceTimerRef.current);
    }
    
    if (searchTerm.trim() === '') {
      setSearchResults([]);
      setIsLoading(false);
      return;
    }

    debounceTimerRef.current = setTimeout(async () => {
      setIsLoading(true);
      try {
        const term = searchTerm.trim();
        // 콤마가 있으면 재료 검색 API, 없으면 제목 검색 API 호출
        const isIngredientSearch = term.includes(',');
        const endpoint = isIngredientSearch ? '/api/search/ingredients' : '/api/search/title';
        
        console.log(`🔍 자동완성 API 호출 (${isIngredientSearch ? '재료' : '제목'}):`, term);
        
        const response = await api.get(endpoint, {
          params: { 
            q: term, 
            page: 0,
            size: 5 
          }
        });
        
        const recipes = response.data.content || [];
        
        setSearchResults(recipes.map(recipe => ({
          id: recipe.rcpSno,
          title: recipe.rcpTtl || '제목 없음',
          type: recipe.ckgNm || ''
        })));
        
      } catch (error) {
        console.error("❌ 자동완성 검색 실패:", error);
        setSearchResults([]);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(debounceTimerRef.current);
  }, [searchTerm]);

  // 로그아웃
  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    sessionStorage.clear();
    
    setIsLoggedIn(false);
    setIsDropdownOpen(false);
    
    alert('로그아웃되었습니다.');
    navigate('/');
  };

  // 프로필 아이콘 클릭
  const handleProfileIconClick = (e) => {
    e.stopPropagation();
    if (isLoggedIn) {
      setIsDropdownOpen(prev => !prev);
    } else {
      navigate('/login');
    }
  };

  // 자동완성 항목 클릭
  const handleAutocompleteClick = (recipeId) => {
    navigate(`/details/${recipeId}`);
    setSearchResults([]);
    setSearchTerm('');
  };

  // 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
      if (searchWrapperRef.current && !searchWrapperRef.current.contains(event.target)) {
        setSearchResults([]);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // 페이지 이동 시 초기화
  useEffect(() => {
    setSearchTerm('');
    setSearchResults([]); 
    setIsDropdownOpen(false);
  }, [location.pathname]);

  return (
    <header className="header-container">
      <div className="header-content">
        <Link to="/" className="logo">
          원룸 레시피
        </Link>
        
        <div className="search-bar-wrapper" ref={searchWrapperRef}>
          <form className="search-bar" onSubmit={handleSearchSubmit}>
            <input 
              type="text" 
              placeholder="레시피명 또는 재료(쉼표로 구분)" 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              autoComplete="off"
            />
            <button type="submit" className="search-submit-btn">
              <FaSearch />
            </button>
          </form>

          {searchTerm && (isLoading || searchResults.length > 0) && (
            <div className="autocomplete-dropdown">
              {isLoading ? (
                <div className="autocomplete-item loading">
                  <div className="loading-spinner"></div>
                  검색 중...
                </div>
              ) : searchResults.length > 0 ? (
                <>
                  {searchResults.map(recipe => (
                    <div 
                      key={recipe.id}
                      className="autocomplete-item"
                      onClick={() => handleAutocompleteClick(recipe.id)}
                    >
                      <FaSearch className="search-icon" />
                      <div className="recipe-info-autocomplete">
                        <span className="recipe-title">{recipe.title}</span>
                        {recipe.type && <span className="recipe-type-small">{recipe.type}</span>}
                      </div>
                    </div>
                  ))}
                  <div 
                    className="autocomplete-item view-all"
                    onClick={handleSearchSubmit}
                  >
                    <FaSearch className="search-icon" />
                    <span className="view-all-text">
                      "{searchTerm}" 전체 검색 결과 보기
                    </span>
                  </div>
                </>
              ) : null}
            </div>
          )}
          
          {searchTerm && !isLoading && searchResults.length === 0 && (
            <div className="autocomplete-dropdown">
              <div className="autocomplete-item no-results">
                "{searchTerm}" 검색 결과가 없습니다.
              </div>
            </div>
          )}
        </div>
        
        <div className="user-menu">
          <div className="profile-menu-container" ref={dropdownRef}>
            <button
              type="button"
              className="icon-link profile" 
              title={isLoggedIn ? "마이페이지" : "로그인"}
              onClick={handleProfileIconClick}
            >
              <FaUserCircle className="icon" />
            </button>

            {isLoggedIn && isDropdownOpen && (
              <div className="profile-dropdown">
                <div 
                  className="dropdown-item" 
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    setIsDropdownOpen(false);
                    navigate('/mypage');
                  }}
                  style={{ cursor: 'pointer' }}
                >
                  <VscAccount /> My
                </div>
                
                <div className="dropdown-item disabled">
                  <FaBell /> 알림
                </div>
                <button 
                  type="button" 
                  className="dropdown-item" 
                  onClick={handleLogout}
                >
                  <VscSignOut /> 로그아웃
                </button>
              </div>
            )}
          </div>

          <Link 
            to="/recipe/write" 
            className="icon-link edit" 
            title="레시피 작성"
          >
            <FaEdit className="icon" />
          </Link>
        </div>
      </div>
    </header>
  );
}

export default Header;