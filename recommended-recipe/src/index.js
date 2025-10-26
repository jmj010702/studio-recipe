import React from 'react';
import './RecipePage.css';


const data1 = [
  { id: 1, title: "명란마요초밥", desc: "도시락에 빠질 수 없는 꿀유부초밥!", image: "https://via.placeholder.com/300x150?text=명란마요초밥", views: 120, likes: 30, date: '2025-10-14' },
  { id: 2, title: "아시안닭꼬치", desc: "저녁 술안주로 딱!", image: "https://via.placeholder.com/300x150?text=아시안닭꼬치", views: 250, likes: 80, date: '2025-10-13' }
];

const data2 = [
  { id: 3, title: "불맛 찹스테이크", desc: "달콤짭짤한 소스의 매력!", image: "https://via.placeholder.com/300x150?text=찹스테이크", views: 500, likes: 150, date: '2025-10-12' },
  { id: 4, title: "호텔 파스타", desc: "집에서 즐기는 호텔급 맛", image: "https://via.placeholder.com/300x150?text=파스타", views: 450, likes: 120, date: '2025-10-11' }
];

const data3 = [
  { id: 5, title: "가을 전골", desc: "따뜻한 국물의 매력", image: "https://via.placeholder.com/300x150?text=전골", views: 300, likes: 95, date: '2025-10-10' },
  { id: 6, title: "제철 과일 샐러드", desc: "상큼하고 건강한 한 끼", image: "https://via.placeholder.com/300x150?text=샐러드", views: 180, likes: 60, date: '2025-10-09' }
];


function RecipePage() {
  

  const sortRecipes = (containerId, criteria) => {

    alert(`${containerId}를 ${criteria} 기준으로 정렬합니다.`);
    console.log(`Sorting ${containerId} by ${criteria}`);
  };

  return (

    <>
      <header>
        <div className="header-left">
          <a href="/">
            <h1>원룸 레시피</h1>
          </a>
        </div>
        <div className="header-center">
          <input type="text" placeholder="검색어를 입력하세요" />
        </div>
        <div className="header-right">
          <a href="/mypage" className="icon-btn mypage" title="마이페이지">
            <i className="fa-solid fa-user"></i>
          </a>
          <a href="/register" className="icon-btn register" title="레시피 등록">
            <i className="fa-solid fa-pen"></i>
          </a>
        </div>
      </header>

      <div className="banner">
        {                                }
        <div style={{ background: '#aee' }}>가을 맞이 레시피</div>
        <div style={{ background: '#fea' }}>나의 레시피</div>
        <div style={{ background: '#eaf' }}>특별한 요리 모음</div>
      </div>

      <section>
        <div className="section-header">
          <h2>금일의 레시피 추천</h2>
          <div className="sort-buttons">
            <button onClick={() => sortRecipes('recipeList1', 'views')}>조회수</button>
            <button onClick={() => sortRecipes('recipeList1', 'likes')}>추천수</button>
            <button onClick={() => sortRecipes('recipeList1', 'date')}>최신순</button>
          </div>
        </div>
        <div className="recipes" id="recipeList1">
          {                                             }
          {data1.map(recipe => (
            <div key={recipe.id} className="recipe-card">
              <img src={recipe.image} alt={recipe.title} />
              <div className="info">
                <h3>{recipe.title}</h3>
                <p>{recipe.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section>
        <div className="section-header">
          <h2>인기 Top 10 레시피</h2>
          <div className="sort-buttons">
            <button onClick={() => sortRecipes('recipeList2', 'views')}>조회수</button>
            <button onClick={() => sortRecipes('recipeList2', 'likes')}>추천수</button>
            <button onClick={() => sortRecipes('recipeList2', 'date')}>최신순</button>
          </div>
        </div>
        <div className="recipes" id="recipeList2">
          {data2.map(recipe => (
            <div key={recipe.id} className="recipe-card">
              <img src={recipe.image} alt={recipe.title} />
              <div className="info">
                <h3>{recipe.title}</h3>
                <p>{recipe.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section>
        <div className="section-header">
          <h2>이런 레시피는 어때요?</h2>
          <div className="sort-buttons">
            <button onClick={() => sortRecipes('recipeList3', 'views')}>조회수</button>
            <button onClick={() => sortRecipes('recipeList3', 'likes')}>추천수</button>
            <button onClick={() => sortRecipes('recipeList3', 'date')}>최신순</button>
          </div>
        </div>
        <div className="recipes" id="recipeList3">
          {data3.map(recipe => (
            <div key={recipe.id} className="recipe-card">
              <img src={recipe.image} alt={recipe.title} />
              <div className="info">
                <h3>{recipe.title}</h3>
                <p>{recipe.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>
    </>
  );
}

export default RecipePage;