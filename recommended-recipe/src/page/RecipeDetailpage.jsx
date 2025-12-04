import React, { useState, useEffect } from 'react';
<<<<<<< HEAD
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
=======
import { useParams, useNavigate } from 'react-router-dom';
import { FaHeart, FaStar, FaArrowLeft, FaTrash } from 'react-icons/fa';
import api from '../api/axios';
import './RecipeDetailPage.css'; 

function RecipeDetailPage() {
  const { recipeId } = useParams(); 
  const navigate = useNavigate();
  
  const BASE_URL = 'http://localhost:8080/studio-recipe';

  const [recipe, setRecipe] = useState(null);
  const [isLiked, setIsLiked] = useState(false);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [isMyRecipe, setIsMyRecipe] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 1. 레시피 데이터 가져오기
  useEffect(() => {
    const fetchRecipe = async () => {
      if (!recipeId) {
        setError('레시피 ID가 없습니다.');
        setLoading(false);
        return;
      }

      setLoading(true);
      setError(null);
      
      try {
        console.log('🔍 레시피 데이터 로드 시작 - ID:', recipeId);
        console.log('🔍 요청 URL:', `/api/details/${recipeId}`);
        const response = await api.get(`/api/details/${recipeId}`);
        
        let recipeData, isLikedData, isBookmarkedData, isMyRecipeData;
        const resData = response.data.data || response.data;

        if (resData.recipe) {
            recipeData = resData.recipe;
            isLikedData = resData.isLiked;
            isBookmarkedData = resData.isBookmarked;
            isMyRecipeData = resData.isMyRecipe;
        } else {
            recipeData = resData;
            isLikedData = false;
            isBookmarkedData = false;
            isMyRecipeData = false;
        }
        
        if (!recipeData) {
          throw new Error('레시피 데이터가 없습니다.');
        }
        
        setRecipe(recipeData);
        setIsLiked(!!isLikedData);
        setIsBookmarked(!!isBookmarkedData);
        setIsMyRecipe(!!isMyRecipeData);
        setLikeCount(recipeData.rcmmCnt || recipeData.likeCount || 0);
        
        console.log('✅ 레시피 데이터 로드 완료:', {
          recipeId,
          isLiked: !!isLikedData,
          isBookmarked: !!isBookmarkedData,
          likeCount: recipeData.rcmmCnt || recipeData.likeCount || 0
        });
        
        // 🔍 전체 레시피 객체 출력
        console.log('🔍 전체 레시피 데이터:', recipeData);
        console.log('🔍 모든 키:', Object.keys(recipeData));
        
      } catch (error) {
        console.error("❌ 레시피 불러오기 실패:", error);
        if (error.response) {
            setError(`오류가 발생했습니다. (${error.response.status})`);
        } else {
          setError("서버에 연결할 수 없습니다.");
        }
      } finally {
        setLoading(false);
      }
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
    };

    fetchRecipe();
  }, [recipeId]);
<<<<<<< HEAD
=======

  // 2. 찜하기 핸들러
  const handleBookmark = async () => {
    if (!recipeId) return;

    try {
      const response = await api.post(`/api/details/bookmarks`, null, {
        params: { recipe_id: recipeId }
      });
      
      const newStatus = response.data.isBookmarked;
      setIsBookmarked(newStatus);
      
      console.log('⭐ 찜하기 상태 변경:', newStatus);
      alert(newStatus ? '레시피를 찜했습니다! ⭐' : '찜을 취소했습니다.');
      
    } catch (error) {
      console.error("❌ 찜하기 실패:", error);
      if (error.response?.status === 401) {
        if (window.confirm('로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?')) {
          navigate('/login');
        }
      } else {
        alert('찜하기 처리에 실패했습니다.');
      }
    }
  };

  // 3. 좋아요 핸들러
  const handleLike = async () => {
    if (!recipeId) return;

    try {
      const response = await api.post(`/api/details/likes`, null, {
        params: { recipe_id: recipeId }
      });
      
      const newIsLiked = response.data.isLiked;
      const newLikeCount = response.data.likeCount;
      
      setIsLiked(newIsLiked);
      setLikeCount(newLikeCount);
      
      console.log('❤️ 좋아요 상태 변경:', {
        isLiked: newIsLiked,
        likeCount: newLikeCount
      });
      
    } catch (error) {
      console.error("❌ 좋아요 실패:", error);
      if (error.response?.status === 401) {
        if (window.confirm('로그인이 필요합니다. 로그인하시겠습니까?')) {
          navigate('/login');
        }
      } else {
        alert('좋아요 처리에 실패했습니다.');
      }
    }
  };

  // 4. 완료 핸들러
  const handleComplete = async () => {
    if (!recipeId) return;

    try {
      await api.post(`/api/details/completion`, null, {
        params: { recipe_id: recipeId }
      });
      
      alert('레시피 요리를 완료했습니다! 🎉');
      
    } catch (error) {
      console.error("❌ 사용 완료 실패:", error);
      if (error.response?.status === 401) {
         if (window.confirm('로그인이 필요합니다. 이동하시겠습니까?')) {
           navigate('/login');
         }
      } else {
        alert('처리 중 오류가 발생했습니다.');
      }
    }
  };

  // 5. 삭제 핸들러
  const handleDelete = async () => {
    if (!window.confirm("정말로 이 레시피를 삭제하시겠습니까? 삭제 후에는 복구할 수 없습니다.")) {
        return;
    }

    try {
        const response = await api.delete(`/api/details/${recipeId}`);

        if (response.status === 200) {
            alert("레시피가 삭제되었습니다.");
            navigate('/');
        }
    } catch (error) {
        console.error("❌ 삭제 실패:", error);
        const errorMessage = error.response?.data?.error || "삭제에 실패했습니다.";
        alert(errorMessage);
    }
  };
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a

  if (loading) {
    return (
      <div className="detail-page-container">
<<<<<<< HEAD
        <p>레시피를 불러오는 중...</p>
=======
        <div className="loading-spinner">
          <p>레시피를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="detail-page-container">
        <div className="error-message">
          <h2>오류 발생</h2>
          <p>{error}</p>
          <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
        </div>
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className="detail-page-container">
<<<<<<< HEAD
        <p>해당 레시피를 찾을 수 없습니다.</p>
      </div>
    );
=======
        <div className="error-message">
          <p>해당 레시피를 찾을 수 없습니다.</p>
          <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
        </div>
      </div>
    );
  }

  const title = recipe.rcpTtl || '제목 없음';
  
  // 이미지 URL 생성 로직
  let imageUrl = '/default-recipe-image.jpg';

  if (recipe.rcpImgUrl) {
    if (recipe.rcpImgUrl.startsWith('http')) {
       imageUrl = recipe.rcpImgUrl;
    } else {
       let cleanFileName = recipe.rcpImgUrl.replace(/^(\/|\\)?(images)?(\/|\\)?/i, '');
       
       if (cleanFileName.startsWith('/')) {
         cleanFileName = cleanFileName.substring(1);
       }

       imageUrl = `${BASE_URL}/images/${cleanFileName}`;
    }
  }

  const cookingName = recipe.ckgNm || recipe.ckg_nm || '';
  const difficulty = recipe.ckgDodfNm || recipe.ckg_dodf_nm || '';
  const servings = recipe.ckgInbunNm || recipe.ckg_inbun_nm || '';
  const cookingTime = recipe.ckgTimeNm || recipe.ckg_time_nm || '';
  const method = recipe.ckgMthActoNm || recipe.ckg_mth_acto_nm || '';
  const viewCount = recipe.inqCnt !== undefined ? recipe.inqCnt : (recipe.viewCount || 0);

  // 🔧 재료 파싱 로직 개선
  let ingredientList = [];
  
  // 🔍 디버깅: 모든 가능한 재료 필드 확인
  console.log('🔍 전체 recipe 키:', Object.keys(recipe));
  console.log('🔍 recipe 전체:', recipe);
  
  // 여러 가능한 키 이름 체크 (모든 변형 추가)
  const ingredientsRaw = recipe.ckgMtrlCn || 
                        recipe.ckg_mtrl_cn || 
                        recipe.ckgMtrlActoCn ||
                        recipe.ckg_mtrl_acto_cn ||
                        recipe.ingredients || 
                        recipe.rcpPartsDtls ||
                        recipe.rcp_parts_dtls ||
                        '';
  
  console.log('🔍 재료 원본 데이터:', ingredientsRaw);
  console.log('🔍 재료 데이터 타입:', typeof ingredientsRaw);
  
  if (ingredientsRaw && ingredientsRaw.length > 0) {
    // [곤약떡 재료], [초콜렛 소 재료] 등의 제목 텍스트 제거
    let cleanedIngredients = ingredientsRaw
      .replace(/\[.*?\]/g, '') // 대괄호로 감싸진 텍스트 모두 제거
      .trim();
    
    // | 로 분리하고 빈 값 제거
    ingredientList = cleanedIngredients
      .split('|')
      .map(item => item.trim())
      .filter(item => item.length > 0);
    
    console.log('📋 파싱된 재료 목록:', ingredientList);
    console.log('📋 재료 개수:', ingredientList.length);
  } else {
    console.warn('⚠️ 재료 데이터가 비어있거나 없습니다');
    console.warn('⚠️ 사용 가능한 모든 키:', Object.keys(recipe));
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
  }

  return (
    <div className="detail-page-container">
<<<<<<< HEAD
=======
      
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
      <div className="recipe-header">
        <h1>{title}</h1>
        {cookingName && <p className="cooking-name">{cookingName}</p>}
        
        <div className="recipe-meta">
          {difficulty && <span className="meta-item">난이도: {difficulty}</span>}
          {servings && <span className="meta-item">인분: {servings}</span>}
          {cookingTime && <span className="meta-item">조리시간: {cookingTime}</span>}
        </div>
      </div>

<<<<<<< HEAD
      {recipe.imageUrl && (
        <img
          src={recipe.imageUrl}
          alt={recipe.title}
          className="recipe-main-image"
        />
      )}
=======
      <button 
        onClick={handleBookmark} 
        className={`bookmark-btn ${isBookmarked ? 'bookmarked' : ''}`}
        title={isBookmarked ? '찜 취소' : '찜하기'}
      >
        {isBookmarked ? <FaStar /> : <FaStar style={{ color: '#ccc' }} />}
      </button>

      <div className="recipe-img-wrapper">
          <img src={imageUrl} alt={title} className="recipe-main-image" />
      </div>
      
      <div className="recipe-actions">
        <button 
          onClick={handleLike} 
          className={`like-btn ${isLiked ? 'liked' : ''}`}
          style={{
            backgroundColor: isLiked ? '#ffe6e6' : 'white',
            border: isLiked ? '2px solid #ff4444' : '2px solid #ddd',
            color: isLiked ? '#ff4444' : '#666',
            padding: '10px 20px',
            borderRadius: '8px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            fontSize: '1rem',
            fontWeight: '600',
            transition: 'all 0.3s'
          }}
        >
          <FaHeart 
            style={{ 
              color: isLiked ? '#ff4444' : '#ccc',
              transition: 'all 0.3s'
            }} 
          /> 
          좋아요 {likeCount}
        </button>

        <button onClick={handleComplete} className="complete-btn">
          ✅ 요리 완료
        </button>
        
        <span className="view-count">조회수: {viewCount}</span>
      </div>
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a

      <div className="recipe-content-box">
        <h2>재료</h2>
        <ul className="ingredient-list">
<<<<<<< HEAD
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
=======
          {ingredientList.length > 0 ? (
            ingredientList.map((item, index) => (
              <li key={index}>{item}</li>
            ))
          ) : (
            <li>재료 정보가 없습니다.</li>
          )}
        </ul>
      </div>

      {method && (
        <div className="recipe-content-box">
          <h2>조리 방법</h2>
          <p className="cooking-method">{method}</p>
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
          <button onClick={() => navigate(-1)} className="back-btn">
            <FaArrowLeft /> 뒤로 가기
          </button>

          {isMyRecipe && (
            <button 
                onClick={handleDelete} 
                style={{
                    backgroundColor: '#ff4d4f',
                    color: 'white',
                    border: 'none',
                    borderRadius: '8px',
                    padding: '10px 20px',
                    fontSize: '1rem',
                    fontWeight: 'bold',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '5px'
                }}
            >
                <FaTrash /> 삭제하기
            </button>
          )}
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
      </div>
    </div>
  );
}

export default RecipeDetailPage;
