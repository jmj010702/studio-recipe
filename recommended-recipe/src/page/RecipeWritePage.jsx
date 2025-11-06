// src/page/RecipeWritePage.jsx
import React, { useState } from 'react'; // ★★★ 오타 수정 ★★★
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { FaPlus, FaTrash } from 'react-icons/fa'; // 아이콘 임포트
import './RecipeWritePage.css'; // CSS 파일

function RecipeWritePage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [intro, setIntro] = useState('');
  const [url, setUrl] = useState('');
  const [tags, setTags] = useState('');
  
  // 1. 재료 목록을 배열 state로 관리
  const [ingredients, setIngredients] = useState([
    { name: '', amount: '', unit: '', note: '' }
  ]);

  // 2. 재료 입력란 변경 핸들러
  const handleIngredientChange = (index, event) => {
    const values = [...ingredients];
    values[index][event.target.name] = event.target.value;
    setIngredients(values);
  };

  // 3. 재료 입력란 추가
  const addIngredientField = () => {
    setIngredients([...ingredients, { name: '', amount: '', unit: '', note: '' }]);
  };

  // 4. 재료 입력란 삭제
  const removeIngredientField = (index) => {
    // 최소 1개의 입력란은 유지
    if (ingredients.length <= 1) return; 
    const values = [...ingredients];
    values.splice(index, 1);
    setIngredients(values);
  };

  // 5. 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    const recipeData = {
      title,
      introduction: intro,
      videoUrl: url,
      tags,
      ingredients
    };
    
    // TODO: '임시' 로직을 '실제' API 호출로 변경
    try {
      // (Mock) 임시 로직
      console.log(" (Mock) 전송할 레시피 데이터:", recipeData);
      
      // (실제) API 호출
      // await api.post('/api/recipes/new', recipeData); 
      
      alert('레시피가 성공적으로 등록되었습니다! (임시)');
      navigate('/mypage'); // 등록 후 마이페이지로 이동

    } catch (error) {
      console.error('레시피 등록 실패:', error);
      alert('레시피 등록 중 오류가 발생했습니다.');
    }
  };

  return (
    // 로그인/회원가입 폼과 유사한 레이아웃
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
                <input type="text" name="name" placeholder="재료 이름" value={field.name} onChange={e => handleIngredientChange(index, e)} />
                <input type="text" name="amount" placeholder="수량" value={field.amount} onChange={e => handleIngredientChange(index, e)} />
                <input type="text" name="unit" placeholder="단위" value={field.unit} onChange={e => handleIngredientChange(index, e)} />
                <input type="text" name="note" placeholder="비고" value={field.note} onChange={e => handleIngredientChange(index, e)} />
                <button 
                  type="button" 
                  className="remove-btn" 
                  onClick={() => removeIngredientField(index)}
                  disabled={ingredients.length <= 1} // 1개일 땐 비활성화
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