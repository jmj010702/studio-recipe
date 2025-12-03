// src/page/RecipeWritePage.jsx
import React, { useState } from 'react';
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
    { name: '', amount: '', unit: '', note: '' },
  ]);

  const handleIngredientChange = (index, event) => {
    const values = [...ingredients];
    values[index][event.target.name] = event.target.value;
    setIngredients(values);
  };

  const addIngredientField = () => {
    setIngredients([
      ...ingredients,
      { name: '', amount: '', unit: '', note: '' },
    ]);
  };

  const removeIngredientField = (index) => {
    if (ingredients.length <= 1) return;
    const values = [...ingredients];
    values.splice(index, 1);
    setIngredients(values);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const recipeData = {
      title,
      introduction: intro,
      videoUrl: url,
      tags,
      ingredients,
    };

    try {
      await api.post('recipes/new', recipeData);
      alert('레시피가 성공적으로 등록되었습니다!');
      navigate('/mypage');
    } catch (error) {
      console.error('레시피 등록 실패:', error);
      alert('레시피 등록 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="form-page-container">
      <div className="form-box recipe-form-box">
        <h2>레시피 쓰기</h2>

        <form onSubmit={handleSubmit}>
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

          <div className="form-group">
            <label htmlFor="intro">레시피 소개</label>
            <textarea
              id="intro"
              placeholder="레시피가 생기게 된 이유를 설명해주세요!"
              rows="5"
              value={intro}
              onChange={(e) => setIntro(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label>재료 정보</label>
            {ingredients.map((field, index) => (
              <div className="ingredient-row" key={index}>
                <input
                  type="text"
                  name="name"
                  placeholder="재료 이름"
                  value={field.name}
                  onChange={(e) => handleIngredientChange(index, e)}
                />
                <input
                  type="text"
                  name="amount"
                  placeholder="수량"
                  value={field.amount}
                  onChange={(e) => handleIngredientChange(index, e)}
                />
                <input
                  type="text"
                  name="unit"
                  placeholder="단위"
                  value={field.unit}
                  onChange={(e) => handleIngredientChange(index, e)}
                />
                <input
                  type="text"
                  name="note"
                  placeholder="비고"
                  value={field.note}
                  onChange={(e) => handleIngredientChange(index, e)}
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
            <button
              type="button"
              className="add-btn"
              onClick={addIngredientField}
            >
              <FaPlus /> 재료 추가
            </button>
          </div>

          <div className="form-group">
            <label htmlFor="url">레시피 동영상 URL</label>
            <input
              type="text"
              id="url"
              placeholder="https://"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
            />
            <small>
              레시피 동영상 등록은 Youtube, 인스타그램만 가능합니다.
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="tags">태그</label>
            <input
              type="text"
              id="tags"
              placeholder="예) #소고기 #미역국"
              value={tags}
              onChange={(e) => setTags(e.target.value)}
            />
            <small>
              주재료, 목적, 효능, 대상 등을 태그로 남겨주세요. (최대 10개)
            </small>
          </div>

          <button type="submit" className="submit-btn">
            등록하기
          </button>
        </form>
      </div>
    </div>
  );
}

export default RecipeWritePage;
