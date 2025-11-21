// src/page/RecipeDetailPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
// import api from '../api/axios'; // (실제 API)
import './RecipeDetailPage.css'; // (4번에서 만들 CSS)

// --- (Mock Data) MainPage.jsx에 있던 임시 데이터를 여기로 가져옵니다. ---
// (실제로는 API로 1개만 조회하지만, 임시 로직을 위해 전체 데이터를 사용합니다.)
const MOCK_ALL_RECIPES = [
  { id: 1, title: "명란마요초밥", description: "도시락에 빠질 수 없는 공유부초밥!", imageUrl: "https://via.placeholder.com/800x450.png?text=Mentaiko+Sushi", ingredients: [{name: '명란', amount: '1개'}, {name: '밥', amount: '1공기'}], steps: ["밥에 양념을 합니다.", "유부를 조립니다.", "명란마요를 올립니다."]},
  { id: 2, title: "아시안 닭꼬치", description: "저녁 술안주로 딱!", imageUrl: "https://via.placeholder.com/800x450.png?text=Asian+Chicken+Skewer", ingredients: [{name: '닭다리살', amount: '300g'}, {name: '간장', amount: '2큰술'}], steps: ["닭을 손질합니다.", "꼬치에 꿰어 굽습니다."]},
  { id: 3, title: "불맛 잡채스테이크", description: "달콤짭짤한 소스의 매력!", imageUrl: "https://via.placeholder.com/800x450.png?text=Japchae+Steak", ingredients: [{name: '소고기', amount: '200g'}, {name: '당면', amount: '50g'}], steps: ["고기를 굽습니다.", "야채와 당면을 볶습니다."]},
  { id: 4, title: "호텔 파스타", description: "집에서 즐기는 호텔급 맛", imageUrl: "https://via.placeholder.com/800x450.png?text=Hotel+Pasta", ingredients: [{name: '파스타면', amount: '100g'}, {name: '새우', amount: '5마리'}], steps: ["면을 삶습니다.", "재료를 볶습니다."]}
];
// --- (Mock Data 끝) ---

function RecipeDetailPage() {
  // 1. URL 파라미터에서 :recipeId 값을 가져옵니다.
  const { recipeId } = useParams(); 
  const [recipe, setRecipe] = useState(null); // 레시피 데이터를 저장할 state
  const [loading, setLoading] = useState(true);

  // 2. recipeId가 변경될 때마다 레시피 데이터를 불러옵니다.
  useEffect(() => {
    const fetchRecipe = async () => {
      setLoading(true);
      try {
        // --- (Mock Logic) 임시 데이터에서 ID로 레시피 찾기 ---
        // (URL에서 받은 recipeId는 문자열이므로 숫자로 변환합니다.)
        const foundRecipe = MOCK_ALL_RECIPES.find(r => r.id === parseInt(recipeId));
        
        // (네트워크 딜레이 0.5초 흉내)
        setTimeout(() => {
          if (foundRecipe) {
            setRecipe(foundRecipe);
          } else {
            setRecipe(null); // 레시피 없음
          }
          setLoading(false);
        }, 500);
        // --- (Mock Logic 끝) ---

        /*
        // --- (실제 API 호출 로직) ---
        const response = await api.get(`/api/recipes/${recipeId}`);
        setRecipe(response.data);
        setLoading(false);
        */

      } catch (error) {
        console.error("레시피 상세 정보를 불러오는 데 실패했습니다:", error);
        setLoading(false);
      }
    };

    fetchRecipe();
  }, [recipeId]); // recipeId가 바뀔 때마다 다시 실행

  // 3. 로딩 중일 때 표시
  if (loading) {
    return <div className="detail-page-container"><p>레시피를 불러오는 중...</p></div>;
  }

  // 4. 레시피가 없을 때 표시
  if (!recipe) {
    return <div className="detail-page-container"><p>해당 레시피를 찾을 수 없습니다.</p></div>;
  }

  // 5. 로딩 완료 후 레시피 표시
  return (
    <div className="detail-page-container">
      {/* (1) 제목 및 설명 */}
      <div className="recipe-header">
        <h1>{recipe.title}</h1>
        <p>{recipe.description}</p>
      </div>

      {/* (2) 메인 이미지 */}
      <img src={recipe.imageUrl} alt={recipe.title} className="recipe-main-image" />

      {/* (3) 재료 */}
      <div className="recipe-content-box">
        <h2>재료</h2>
        <ul className="ingredient-list">
          {recipe.ingredients.map((item, index) => (
            <li key={index}>
              <span className="ingredient-name">{item.name}</span>
              <span className="ingredient-amount">{item.amount}</span>
            </li>
          ))}
        </ul>
      </div>

      {/* (4) 조리 순서 */}
      <div className="recipe-content-box">
        <h2>조리 순서</h2>
        <ol className="step-list">
          {recipe.steps.map((step, index) => (
            <li key={index}>{step}</li>
          ))}
        </ol>
      </div>
    </div>
  );
}

export default RecipeDetailPage;