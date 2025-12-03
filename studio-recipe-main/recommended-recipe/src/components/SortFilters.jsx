// src/components/SortFilters.jsx
import React, { useState } from 'react';
import './SortFilters.css'; // SortFilters CSS 임포트

function SortFilters({ onSortChange }) {
  // 'views' (조회수), 'likes' (추천수), 'newest' (최신순) - 백엔드 API와 일치시킬 것
  const [activeFilter, setActiveFilter] = useState('views'); 

  const handleClick = (filter) => {
    setActiveFilter(filter);
    onSortChange(filter); // 부모 컴포넌트에 알림
  };

  return (
    <div className="sort-filters">
      <span 
        className={`filter-item ${activeFilter === 'views' ? 'active' : ''}`}
        onClick={() => handleClick('views')}
      >
        조회수
      </span>
      <span className="divider">|</span>
      <span 
        className={`filter-item ${activeFilter === 'likes' ? 'active' : ''}`}
        onClick={() => handleClick('likes')}
      >
        추천수
      </span>
      <span className="divider">|</span>
      <span 
        className={`filter-item ${activeFilter === 'newest' ? 'active' : ''}`}
        onClick={() => handleClick('newest')}
      >
        최신순
      </span>
    </div>
  );
}

export default SortFilters;