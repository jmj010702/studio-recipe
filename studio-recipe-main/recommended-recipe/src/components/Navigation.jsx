// src/components/Navigation.jsx
import React from 'react';
import './Navigation.css';

function Navigation() {
  const onNavClick = (category) => {
    console.log(`${category} 클릭`);
    // navigate(`/category/${category}`);
  };

  return (
    // 1. (배경) 100% 너비의 <nav> 태그
    <nav className="nav-container">
      {/* 2. (콘텐츠) 중앙 정렬되는 내부 컨테이너 */}
      <div className="nav-content">
        <button 
          className="nav-button seasonal"
          onClick={() => onNavClick('seasonal')}
        >
          가을 맞이 레시피
        </button>
        <button 
          className="nav-button my-recipe"
          onClick={() => onNavClick('my-recipe')}
        >
          나의 레시피
        </button>
        <button 
          className="nav-button special-collection"
          onClick={() => onNavClick('special')}
        >
          특별한 요리 모음
        </button>
      </div>
    </nav>
  );
}

export default Navigation;