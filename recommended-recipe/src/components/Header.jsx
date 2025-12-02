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

  const navigate = useNavigate();
  const location = useLocation(); // 👈 [추가] 현재 경로 감지
  const dropdownRef = useRef(null);
  
  const debounceTimerRef = useRef(null);
  const searchWrapperRef = useRef(null); 

  // ▼▼▼▼▼ [핵심 수정] 로그인 상태 관리 ▼▼▼▼▼
  // 1. isLoggedIn을 state로 관리
  const [isLoggedIn, setIsLoggedIn] = useState(false); 

  // 2. 페이지 이동 시(location)마다 토큰을 확인하여 로그인 상태 갱신
  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token); // 👈 토큰이 있으면 true, 없으면 false
  }, [location]); // 👈 경로가 바뀔 때마다 실행
  // ▲▲▲▲▲ [핵심 수정] 로그인 상태 관리 끝 ▲▲▲▲▲


  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      console.log(`검색 실행: ${searchTerm}`);
      setSearchResults([]);
      setSearchTerm('');
    }
  };

  // ▼▼▼ [핵심 수정] 로그아웃 핸들러 ▼▼▼
  const handleLogout = () => {
    localStorage.removeItem('accessToken'); // 👈 [수정] localStorage 토큰 삭제
    sessionStorage.removeItem('logged_in_user_data'); // (혹시 모르니 임시 데이터도 삭제)
    
    setIsLoggedIn(false); // 👈 state 갱신
    setIsDropdownOpen(false);
    
    alert('로그아웃되었습니다.');
    navigate('/'); // 👈 메인 페이지로 이동
  };
  // ▲▲▲ [핵심 수정] 로그아웃 핸들러 끝 ▲▲▲


  const handleProfileIconClick = () => {
    if (isLoggedIn) {
      setIsDropdownOpen(prev => !prev);
    } else {
      navigate('/login');
    }
  };

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
  }, [dropdownRef, searchWrapperRef]);

  // (페이지 이동 시 검색창 초기화 - 변경 없음)
  useEffect(() => {
    setSearchTerm('');
    setSearchResults([]); 
  }, [location.pathname]);

  // (자동완성 검색 로직 - Mock/API 주석 처리된 상태 유지)
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
        // (Mock) 임시 로직
        console.log(`(Mock) API 호출: /api/recipes/autocomplete?q=${searchTerm.trim()}`);
        const MOCK_RESULTS = [
          { id: 1, title: `${searchTerm} 관련 레시피 1` },
          { id: 2, title: `${searchTerm} 관련 레시피 2 (긴 이름)` },
        ];
        setTimeout(() => {
          setSearchResults(MOCK_RESULTS); 
          setIsLoading(false);
        }, 500);

        /*
        // (실제 API 호출 로직)
        const response = await api.get('/api/recipes/autocomplete', {
          params: { q: searchTerm.trim() }
        });
        setSearchResults(response.data.data); // 👈 백엔드 스펙에 맞게 (예: .data.data)
        setIsLoading(false);
        */
      } catch (error) {
        console.error("자동완성 검색 실패:", error);
        setSearchResults([]); 
        setIsLoading(false);
      }
    }, 300); // 300ms 딜레이

    return () => clearTimeout(debounceTimerRef.current);

  }, [searchTerm]); 


  return (
    // --- (JSX 렌더링 부분은 변경 없음) ---
    // (isLoggedIn이 state를 참조하도록 변경됨)
    <header className="header-container">
      <div className="header-content">
        <Link to="/" className="logo">
          원룸 레시피
        </Link>
        
        <div className="search-bar-wrapper" ref={searchWrapperRef}>
          <form className="search-bar" onSubmit={handleSearchSubmit}>
            <input 
              type="text" 
              placeholder="검색어를 입력하세요" 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onClick={() => { if (searchTerm.trim()) setIsLoading(true); }}
            />
            <button type="submit" className="search-submit-btn">
              <FaSearch />
            </button>
          </form>

          {(searchTerm && (isLoading || searchResults.length > 0)) && (
            <div className="autocomplete-dropdown">
              {isLoading ? (
                <div className="autocomplete-item loading">검색 중...</div>
              ) : (
                searchResults.length > 0 ? (
                  searchResults.map(recipe => (
                    <Link 
                      to={`/recipe/${recipe.id}`} 
                      key={recipe.id}
                      className="autocomplete-item"
                    >
                      {recipe.title}
                    </Link>
                  ))
                ) : (
                  <div className="autocomplete-item loading">검색 결과가 없습니다.</div>
                )
              )}
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

            {/* [수정] isLoggedIn이 (state)를 참조 */}
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
                  onClick={handleLogout} // 👈 수정된 로그아웃 핸들러 연결
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