// src/page/RecipeWritePage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { FaPlus, FaTrash, FaImage } from 'react-icons/fa'; 
import './RecipeWritePage.css'; 

function RecipeWritePage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [intro, setIntro] = useState('');
  const [url, setUrl] = useState('');
  const [tags, setTags] = useState('');
  
  // ✅ 이미지 관련 state 추가
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  
  const [ingredients, setIngredients] = useState([
    { name: '', amount: '', unit: '', note: '' }
  ]);

  // 로그인 확인
  useEffect(() => {
    const token = localStorage.getItem('accessToken'); 
    if (!token) {
      alert('로그인을 해주시기 바랍니다.');
      navigate('/login');
    }
  }, [navigate]);

  // ✅ 이미지 선택 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    
    if (file) {
      // 파일 크기 체크 (예: 5MB 제한)
      if (file.size > 5 * 1024 * 1024) {
        alert('이미지 크기는 5MB 이하로 업로드해주세요.');
        return;
      }

      // 이미지 파일 타입 체크
      if (!file.type.startsWith('image/')) {
        alert('이미지 파일만 업로드 가능합니다.');
        return;
      }

      setImageFile(file);

      // 미리보기 생성
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // ✅ 이미지 삭제 핸들러
  const handleImageRemove = () => {
    setImageFile(null);
    setImagePreview(null);
  };

  const handleIngredientChange = (index, event) => {
    const values = [...ingredients];
    values[index][event.target.name] = event.target.value;
    setIngredients(values);
  };

  const addIngredientField = () => {
    setIngredients([...ingredients, { name: '', amount: '', unit: '', note: '' }]);
  };

  const removeIngredientField = (index) => {
    if (ingredients.length <= 1) return; 
    const values = [...ingredients];
    values.splice(index, 1);
    setIngredients(values);
  };

  // ✅ 폼 제출 핸들러 (이미지 포함)
  const handleSubmit = async (e) => {
    e.preventDefault();

    // 필수 입력 체크
    if (!title.trim()) {
      alert('레시피 제목을 입력해주세요.');
      return;
    }

    try {
      // FormData 생성 (이미지 업로드를 위해)
      const formData = new FormData();
      
      // 텍스트 데이터는 JSON으로 변환하여 추가
      const recipeData = {
        title,
        introduction: intro,
        videoUrl: url,
        tags,
        ingredients
      };
      
      // JSON 데이터를 Blob으로 변환하여 추가
      formData.append('recipe', new Blob([JSON.stringify(recipeData)], {
        type: 'application/json'
      }));
      
      // 이미지 파일 추가 (있는 경우에만)
      if (imageFile) {
        formData.append('image', imageFile);
      }

      // API 호출
      await api.post('/api/recipes/write', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        }
      });
      
      alert('레시피가 성공적으로 등록되었습니다!');
      navigate('/mypage');

    } catch (error) {
      console.error('레시피 등록 실패:', error);
      
      if (error.response) {
        alert(error.response.data.message || '레시피 등록 중 오류가 발생했습니다.');
      } else {
        alert('레시피 등록 중 오류가 발생했습니다.');
      }
    }
  };

  return (
    <div className="form-page-container">
      <div className="form-box recipe-form-box">
        <h2>레시피 쓰기</h2>
        
        <form onSubmit={handleSubmit}>
          
          {/* 레시피 제목 */}
          <div className="form-group">
            <label htmlFor="title">레시피 제목</label>
            <input 
              type="text" 
              id="title" 
              placeholder="예) 소고기 미역국 끓이기" 
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required 
            />
          </div>

          {/* ✅ 레시피 이미지 업로드 */}
          <div className="form-group">
            <label htmlFor="image">레시피 이미지</label>
            
            {/* 이미지 미리보기 */}
            {imagePreview ? (
              <div className="image-preview-container">
                <img 
                  src={imagePreview} 
                  alt="레시피 미리보기" 
                  className="image-preview"
                />
                <button 
                  type="button" 
                  className="image-remove-btn"
                  onClick={handleImageRemove}
                >
                  <FaTrash /> 이미지 삭제
                </button>
              </div>
            ) : (
              <div className="image-upload-container">
                <label htmlFor="image-input" className="image-upload-label">
                  <FaImage className="upload-icon" />
                  <span>이미지 선택하기</span>
                  <small>PC 또는 모바일에서 이미지를 선택할 수 있습니다 (최대 5MB)</small>
                </label>
                <input 
                  type="file" 
                  id="image-input"
                  accept="image/*"
                  onChange={handleImageChange}
                  style={{ display: 'none' }}
                />
              </div>
            )}
          </div>

          {/* 레시피 소개 */}
          <div className="form-group">
            <label htmlFor="intro">레시피 소개</label>
            <textarea 
              id="intro" 
              placeholder="레시피가 생기게 된 이유를 설명해주세요!"
              rows="5"
              value={intro}
              onChange={(e) => setIntro(e.target.value)}
            ></textarea>
          </div>

          {/* 재료 정보 (동적 폼) */}
          <div className="form-group">
            <label>재료 정보</label>
            {ingredients.map((field, index) => (
              <div className="ingredient-row" key={index}>
                <input 
                  type="text" 
                  name="name" 
                  placeholder="재료 이름" 
                  value={field.name} 
                  onChange={e => handleIngredientChange(index, e)} 
                />
                <input 
                  type="text" 
                  name="amount" 
                  placeholder="수량" 
                  value={field.amount} 
                  onChange={e => handleIngredientChange(index, e)} 
                />
                <input 
                  type="text" 
                  name="unit" 
                  placeholder="단위" 
                  value={field.unit} 
                  onChange={e => handleIngredientChange(index, e)} 
                />
                <input 
                  type="text" 
                  name="note" 
                  placeholder="비고" 
                  value={field.note} 
                  onChange={e => handleIngredientChange(index, e)} 
                />
                <button 
                  type="button" 
                  className="remove-btn" 
                  onClick={() => removeIngredientField(index)}
                  disabled={ingredients.length <= 1}
                >
                  <FaTrash />
                </button>
              </div>
            ))}
            <button type="button" className="add-btn" onClick={addIngredientField}>
              <FaPlus /> 재료 추가
            </button>
          </div>

          {/* 동영상 URL */}
          <div className="form-group">
            <label htmlFor="url">레시피 동영상 URL</label>
            <input 
              type="text" 
              id="url" 
              placeholder="https://" 
              value={url}
              onChange={(e) => setUrl(e.target.value)}
            />
            <small>레시피 동영상 등록은 Youtube, 인스타그램만 가능합니다.</small>
          </div>
          
          {/* 태그 */}
          <div className="form-group">
            <label htmlFor="tags">태그</label>
            <input 
              type="text" 
              id="tags" 
              placeholder="예) #소고기 #미역국" 
              value={tags}
              onChange={(e) => setTags(e.target.value)}
            />
            <small>주재료, 목적, 효능, 대상 등을 태그로 남겨주세요. (최대 10개)</small>
          </div>

          {/* 등록하기 버튼 */}
          <button type="submit" className="submit-btn">
            등록하기
          </button>

        </form>
      </div>
    </div>
  );
}

export default RecipeWritePage;