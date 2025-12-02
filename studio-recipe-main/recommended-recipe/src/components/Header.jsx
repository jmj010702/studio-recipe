import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { FaUserCircle, FaEdit, FaBell, FaSearch } from 'react-icons/fa';
import { VscAccount, VscSignOut } from 'react-icons/vsc';
import api from '../api/axios'; // (중요) API 임포트
import './Header.css'; 

function Header() {
  const [searchTerm, setSearchTerm] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  
  const [searchResults, setSearchResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false); 

  const navigate = useNavigate();
  const location = useLocation();
  const dropdownRef = useRef(null);
  
  const debounceTimerRef = useRef(null);
  const searchWrapperRef = useRef(null); 

  const userSession = sessionStorage.getItem('logged_in_user');
  const isLoggedIn = !!userSession; 

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      console.log(`검색 실행: ${searchTerm}`);
      setSearchResults([]);
      setSearchTerm('');
    }
  };

  const handleLogout = () => {
    sessionStorage.removeItem('logged_in_user');
    setIsDropdownOpen(false);
    navigate('/');
  };

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

  // 페이지 이동 시 검색창/결과창 초기화
  useEffect(() => {
    setSearchTerm('');
    setSearchResults([]); 
  }, [location.pathname]);


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
        // (Mock) 임시 로직 (API 대신)
        console.log(`(Mock) API 호출: /api/recipes/autocomplete?q=${searchTerm.trim()}`);
        const MOCK_RESULTS = [
          { id: 1, title: `${searchTerm} 관련 레시피 1` },
          { id: 2, title: `${searchTerm} 관련 레시피 2 (긴 이름)` },
          { id: 3, title: `맛있는 ${searchTerm}` },
          { id: 4, title: `간단한 ${searchTerm} 요리` },
          { id: 5, title: `초간단 ${searchTerm}` },
          { id: 6, title: `스크롤 테스트용 ${searchTerm} 6` },
          { id: 7, title: `스크롤 테스트용 ${searchTerm} 7` },
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
        setSearchResults(response.data); 
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
    <header className="header-container">
      <div className="header-content">
        <Link to="/" className="logo">
          원룸 레시피
        </Link>
        
        {/* 검색창 + 자동완성 결과를 묶는 래퍼 (ref 추가) */}
        <div className="search-bar-wrapper" ref={searchWrapperRef}>
          <form className="search-bar" onSubmit={handleSearchSubmit}>
            <input 
              type="text" 
              placeholder="검색어를 입력하세요" 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              // (추가) 검색창 클릭 시에도 결과가 있다면 보여주기 (선택적)
              onClick={() => { if (searchTerm.trim()) setIsLoading(true); }}
            />
            <button type="submit" className="search-submit-btn">
              <FaSearch />
            </button>
          </form>

          {/* 자동완성 드롭다운 (검색어가 있거나, 로딩중일때) */}
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
                  // 로딩이 끝났는데 결과가 0개일 때
                  <div className="autocomplete-item loading">검색 결과가 없습니다.</div>
                )
              )}
            </div>
          )}
        </div>
        
        {/* 유저 메뉴 */}
        <div className="user-menu">
          {/* 프로필 아이콘 + 드롭다운 영역 (ref 추가) */}
          <div className="profile-menu-container" ref={dropdownRef}>
            <button
              type="button"
              className="icon-link profile" 
              title={isLoggedIn ? "마이페이지" : "로그인"}
              onClick={handleProfileIconClick}
            >
              <FaUserCircle className="icon" />
            </button>

            {/* 프로필 드롭다운 */}
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

          {/* 레시피 작성 아이콘 */}
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