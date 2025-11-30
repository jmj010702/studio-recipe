import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from '../components/Header.jsx';
import MainPage from './MainPage.jsx';
import SignupPage from './SignupPage.jsx';
import LoginPage from './LoginPage.jsx';
import MyPage from './MyPage.jsx';
import RecipeWritePage from './RecipeWritePage.jsx';
import RecipeDetailPage from './RecipeDetailpage.jsx';
import FindIdPage from './FindIdPage.jsx';
import RecipeListPage from './RecipeListPage.jsx';
import FindPasswordPage from './FindPasswordPage.jsx';
import SearchResultPage from './SearchResultPage.jsx';
import './App.css';

function App() {
  return (
    <>
      <Header />
      <Routes>
        {/* 메인 페이지 */}
        <Route path="/" element={<MainPage />} />
        
        {/* 회원 관련 */}
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/find-id" element={<FindIdPage />} />
        <Route path="/find-password" element={<FindPasswordPage />} />
        
        {/* 마이페이지 */}
        <Route path="/mypage" element={<MyPage />} />
        
        {/* 레시피 관련 */}
        <Route path="/recipe/write" element={<RecipeWritePage />} />
        <Route path="/details/:recipeId" element={<RecipeDetailPage />} />
        <Route path="/recipes" element={<RecipeListPage />} />
        
        {/* 검색 */}
        <Route path="/search" element={<SearchResultPage />} />
        
        {/* 404 페이지 (선택사항) */}
        <Route path="*" element={
          <div style={{ textAlign: 'center', padding: '50px' }}>
            <h1>404 - 페이지를 찾을 수 없습니다</h1>
            <a href="/">홈으로 돌아가기</a>
          </div>
        } />
      </Routes>
    </>
  );
}

export default App;