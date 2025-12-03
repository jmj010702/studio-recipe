import React from 'react';
import './SortFilters.css';

function SortFilters({ onSortChange, currentSort = 'recommend' }) {
  const handleClick = (filter) => {
    console.log(`🔀 정렬 버튼 클릭: ${filter}`);
    onSortChange(filter);
  };

  return (
    <div className="sort-filters">
      <span 
        className={`filter-item ${currentSort === 'recommend' ? 'active' : ''}`}
        onClick={() => handleClick('recommend')}
        role="button"
        tabIndex={0}
      >
        추천순
      </span>
      <span className="divider">|</span>
      <span 
        className={`filter-item ${currentSort === 'views' ? 'active' : ''}`}
        onClick={() => handleClick('views')}
        role="button"
        tabIndex={0}
      >
        조회수
      </span>
      <span className="divider">|</span>
      <span 
        className={`filter-item ${currentSort === 'likes' ? 'active' : ''}`}
        onClick={() => handleClick('likes')}
        role="button"
        tabIndex={0}
      >
        추천수
      </span>
      <span className="divider">|</span>
      <span 
        className={`filter-item ${currentSort === 'latest' ? 'active' : ''}`}
        onClick={() => handleClick('latest')}
        role="button"
        tabIndex={0}
      >
        최신순
      </span>
    </div>
  );
}

export default SortFilters;