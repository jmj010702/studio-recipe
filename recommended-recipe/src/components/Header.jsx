// src/components/Header.jsx
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

  // 검색 제출
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
      setSearchResults([]);
      setSearchTerm('');
    }
  };

  // 자동완성 검색
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
        console.log('🔍 검색 API 호출 - keyword:', searchTerm.trim());
        
        const response = await api.get('/api/recipes/search', {
          params: { keyword: searchTerm.trim(), limit: 5 }
        });
        
        console.log('✅ 검색 응답 전체:', response.data);
        
        // 여러 응답 구조 처리
        let recipes = [];
        
        if (response.data.data && Array.isArray(response.data.data)) {
          // 케이스 A: { data: [...] }
          recipes = response.data.data;
        } else if (response.data.recipes && Array.isArray(response.data.recipes)) {
          // 케이스 B: { recipes: [...] }
          recipes = response.data.recipes;
        } else if (Array.isArray(response.data)) {
          // 케이스 C: [...]
          recipes = response.data;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // 케이스 D: 페이징 { content: [...] }
          recipes = response.data.content;
        }
        
        console.log('📦 추출된 레시피 수:', recipes.length);
        
        if (recipes.length > 0) {
          console.log('📋 첫 번째 레시피:', recipes[0]);
        }
        
        setSearchResults(recipes.map(recipe => ({
          id: recipe.rcpSno || recipe.recipeId || recipe.id,
          title: recipe.rcpTtl || recipe.title || '제목 없음',
          type: 'recipe'
        })));
        
      } catch (error) {
        console.error("❌ 자동완성 검색 실패:", error);
        console.error("응답 상태:", error.response?.status);
        console.error("응답 데이터:", error.response?.data);
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
  const handleProfileIconClick = () => {
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

  // 페이지 이동 시 검색창 초기화
  useEffect(() => {
    setSearchTerm('');
    setSearchResults([]); 
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
              placeholder="레시피 검색..." 
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
                searchResults.map(recipe => (
                  <div 
                    key={recipe.id}
                    className="autocomplete-item"
                    onClick={() => handleAutocompleteClick(recipe.id)}
                  >
                    <FaSearch className="search-icon" />
                    <span className="recipe-title">{recipe.title}</span>
                  </div>
                ))
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
                <Link 
                  to="/mypage" 
                  className="dropdown-item" 
                  onClick={() => setIsDropdownOpen(false)}
                >
                  <VscAccount /> My
                </Link>
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