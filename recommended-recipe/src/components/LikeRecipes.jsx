// src/components/mypage/LikedRecipes.jsx
import React from 'react';

function LikedRecipes() {
  return (
    <div>
      <h2>좋아요 누른 레시피</h2>
      <p>내가 '좋아요'를 누른 레시피 목록이 여기에 표시됩니다.</p>
      {/* TODO: /api/my-likes API 호출 및 목록 렌더링 */}
    </div>
  );
}
export default LikedRecipes;