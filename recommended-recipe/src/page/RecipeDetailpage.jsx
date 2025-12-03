// src/page/RecipeDetailPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './RecipeDetailPage.css';

const MOCK_ALL_RECIPES = [
  {
    id: 1,
    title: '명란마요초밥',
    description: '도시락에 빠질 수 없는 공유부초밥!',
    imageUrl: 'https://via.placeholder.com/800x450.png?text=Mentaiko+Sushi',
    ingredients: [
      { name: '명란', amount: '1개' },
      { name: '밥', amount: '1공기' },
    ],
    steps: ['밥에 양념을 합니다.', '유부를 조립니다.', '명란마요를 올립니다.'],
  },
  {
    id: 2,
    title: '아시안 닭꼬치',
    description: '저녁 술안주로 딱!',
    imageUrl:
      'https://via.placeholder.com/800x450.png?text=Asian+Chicken+Skewer',
    ingredients: [
      { name: '닭다리살', amount: '300g' },
      { name: '간장', amount: '2큰술' },
    ],
    steps: ['닭을 손질합니다.', '꼬치에 꿰어 굽습니다.'],
  },
  {
    id: 3,
    title: '불맛 잡채스테이크',
    description: '달콤짭짤한 소스의 매력!',
    imageUrl: 'https://via.placeholder.com/800x450.png?text=Japchae+Steak',
    ingredients: [
      { name: '소고기', amount: '200g' },
      { name: '당면', amount: '50g' },
    ],
    steps: ['고기를 굽습니다.', '야채와 당면을 볶습니다.'],
  },
  {
    id: 4,
    title: '호텔 파스타',
    description: '집에서 즐기는 호텔급 맛',
    imageUrl: 'https://via.placeholder.com/800x450.png?text=Hotel+Pasta',
    ingredients: [
      { name: '파스타면', amount: '100g' },
      { name: '새우', amount: '5마리' },
    ],
    steps: ['면을 삶습니다.', '재료를 볶습니다.'],
  },
];

function RecipeDetailPage() {
  const { recipeId } = useParams();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRecipe = () => {
      setLoading(true);
      const id = parseInt(recipeId, 10);
      const foundRecipe = MOCK_ALL_RECIPES.find((r) => r.id === id);

      setTimeout(() => {
        setRecipe(foundRecipe || null);
        setLoading(false);
      }, 300);
    };

    fetchRecipe();
  }, [recipeId]);

  if (loading) {
    return (
      <div className="detail-page-container">
        <p>레시피를 불러오는 중...</p>
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className="detail-page-container">
        <p>해당 레시피를 찾을 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="detail-page-container">
      <div className="recipe-header">
        <h1>{recipe.title}</h1>
        <p>{recipe.description}</p>
      </div>

      {recipe.imageUrl && (
        <img
          src={recipe.imageUrl}
          alt={recipe.title}
          className="recipe-main-image"
        />
      )}

      <div className="recipe-content-box">
        <h2>재료</h2>
        <ul className="ingredient-list">
          {(recipe.ingredients || []).map((item, index) => (
            <li key={index}>
              <span className="ingredient-name">{item.name}</span>
              <span className="ingredient-amount">{item.amount}</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="recipe-content-box">
        <h2>조리 순서</h2>
        <ol className="step-list">
          {(recipe.steps || []).map((step, index) => (
            <li key={index}>{step}</li>
          ))}
        </ol>
      </div>
    </div>
  );
}

export default RecipeDetailPage;
