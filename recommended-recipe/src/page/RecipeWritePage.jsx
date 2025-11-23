// src/page/RecipeWritePage.jsx
import React, { useState, useEffect } from 'react'; // 👈 1. useEffect 추가
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { FaPlus, FaTrash } from 'react-icons/fa'; 
import './RecipeWritePage.css'; 

function RecipeWritePage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [intro, setIntro] = useState('');
  const [url, setUrl] = useState('');
  const [tags, setTags] = useState('');
  
  const [ingredients, setIngredients] = useState([
    { name: '', amount: '', unit: '', note: '' }
  ]);

  // ▼▼▼▼▼ 2. [추가] 로그인 확인 로직 ▼▼▼▼▼
  useEffect(() => {
    // 1. localStorage에서 토큰을 가져옵니다.
    const token = localStorage.getItem('accessToken'); 

    // 2. 토큰이 없으면 (로그인하지 않았으면)
    if (!token) {
      alert('로그인을 해주시기 바랍니다.');
      // 3. 로그인 페이지로 튕겨냅니다.
      navigate('/login');
    }
  }, [navigate]); // 👈 페이지 로드 시 1회만 실행
  // ▲▲▲▲▲ [추가] 로그인 확인 로직 끝 ▲▲▲▲▲

  // (재료 입력란 변경 핸들러 - 변경 없음)
  const handleIngredientChange = (index, event) => {
    const values = [...ingredients];
    values[index][event.target.name] = event.target.value;
    setIngredients(values);
  };

  // (재료 입력란 추가 - 변경 없음)
  const addIngredientField = () => {
    setIngredients([...ingredients, { name: '', amount: '', unit: '', note: '' }]);
  };

  // (재료 입력란 삭제 - 변경 없음)
  const removeIngredientField = (index) => {
    if (ingredients.length <= 1) return; 
    const values = [...ingredients];
    values.splice(index, 1);
    setIngredients(values);
  };

  // 3. 💡 [수정] 폼 제출 핸들러 (API 연동)
  const handleSubmit = async (e) => {
    e.preventDefault();
    const recipeData = {
      title,
      introduction: intro,
      videoUrl: url,
      tags,
      ingredients
    };
    
    try {
      // 💡 (Mock) 로직은 주석 처리
      // console.log(" (Mock) 전송할 레시피 데이터:", recipeData);
      
      // 💡 (실제) API 호출 활성화
      // (엔드포인트는 백엔드와 협의 필요. /api/recipes/new는 예시)
      await api.post('/api/recipes/write', recipeData); 
      
      alert('레시피가 성공적으로 등록되었습니다!');
      navigate('/mypage'); // 등록 후 마이페이지로 이동

    } catch (error) {
      console.error('레시피 등록 실패:', error);
      alert('레시피 등록 중 오류가 발생했습니다.');
    }
  };

  return (
    // --- (JSX 렌더링 부분은 변경 없음) ---
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

const handleSubmit = async () => {
    // 1. 데이터 유효성 검사
    if (!title || !description) {
      alert("제목과 소개를 입력해주세요.");
      return;
    }

    // 2. 전송할 데이터 객체 만들기
    const recipeData = {
      title: title,
      description: description,
      ingredients: ingredients, // [{name, amount, unit, note}, ...]
      videoUrl: videoUrl,
      tags: tags
    };

    try {
      // 3. 백엔드로 전송 (POST)
      await api.post('/api/recipes/write', recipeData);
      
      alert("레시피가 등록되었습니다!");
      navigate('/'); // 메인 페이지로 이동 (또는 마이페이지)
      
    } catch (error) {
      console.error("등록 실패:", error);
      alert("레시피 등록 중 오류가 발생했습니다.");
    }
  };

export default RecipeWritePage;