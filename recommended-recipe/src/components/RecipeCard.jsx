// src/components/RecipeCard.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './RecipeCard.css';

function RecipeCard({ recipe }) {
  const [imageError, setImageError] = useState(false);

  const recipeId = recipe.recipeId || recipe.rcpSno;
  const title = recipe.title || recipe.rcpTtl || '제목 없음';
  const description = recipe.categoryName || recipe.ckgNm || recipe.ckgDodfNm || '';
  const viewCount = recipe.viewCount || recipe.inqCnt || 0;
  const likeCount = recipe.likeCount || recipe.rcmmCnt || 0;

  // ✅ 이미지 URL 처리 (context path 추가)
  const getImageUrl = () => {
    const rawUrl = recipe.imageUrl || recipe.rcpImgUrl;
    
    if (!rawUrl) {
      console.log('⚠️ 이미지 URL 없음, 기본 이미지 사용');
      return '/default-recipe-image.jpg';
    }

    // 1. 외부 완전한 URL (CSV 데이터)
    if (rawUrl.startsWith('http://') || rawUrl.startsWith('https://')) {
      console.log('✅ 외부 URL:', rawUrl);
      return rawUrl;
    }

    // 2. 로컬 파일 경로 처리
    let localPath = rawUrl;
    
    // /images//images/ 중복 제거
    localPath = localPath.replace(/\/images\/+/g, '/images/');
    
    // /images/로 시작하지 않으면 추가
    if (!localPath.startsWith('/images/')) {
      localPath = '/images/' + localPath;
    }
    
    // 파일 확장자 확인 및 추가
    const hasExtension = /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(localPath);
    if (!hasExtension) {
      console.log('⚠️ 확장자 없음, .jpg 추가:', localPath);
      localPath += '.jpg';
    }
    
    // ⭐ 최종 URL 생성 (context path 포함)
    const finalUrl = `http://localhost:8080/studio-recipe${localPath}`;
    console.log('🖼️ 최종 이미지 URL:', finalUrl);
    
    return finalUrl;
  };

  const imageUrl = getImageUrl();

  const handleImageError = (e) => {
    console.error('❌ 이미지 로드 실패:', imageUrl);
    
    // 한 번만 기본 이미지로 변경
    if (!imageError) {
      setImageError(true);
      e.target.src = '/default-recipe-image.jpg';
    }
  };

  return (
    <div className="recipe-card">
      <Link to={`/details/${recipeId}`}>
        <div className="image-container">
          <img 
            src={imageUrl} 
            alt={title} 
            className="recipe-image"
            onError={handleImageError}
          />
        </div>
        <div className="recipe-info">
          <h3 className="recipe-title">{title}</h3>
          <p className="recipe-description">{description}</p>
          
          <div className="recipe-stats">
            <span className="stat-item">
              👁️ {viewCount.toLocaleString()}
            </span>
            <span className="stat-item">
              ❤️ {likeCount.toLocaleString()}
            </span>
          </div>
        </div>
      </Link>
    </div>
  );
}

export default RecipeCard;