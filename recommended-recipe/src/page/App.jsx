import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from '../components/Header.jsx';
import MainPage from './MainPage.jsx';
import SignupPage from './SignupPage.jsx';
import LoginPage from './LoginPage.jsx'; 
import MyPage from './MyPage.jsx';
import RecipeWritePage from './RecipeWritePage.jsx';
import RecipeDetailPage from './RecipeDetailPage.jsx';
import FindIdPage from './FindIdPage.jsx';
import RecipeListPage from './RecipeListPage.jsx';
import FindPasswordPage from './FindPasswordPage.jsx';
import SearchResultPage from './SearchResultPage.jsx';

import './App.css'; 

function App() {
  return (
    <> 
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/find-id" element={<FindIdPage />} />
          <Route path="/find-password" element={<FindPasswordPage />} />
          
          {/* 레시피 상세 페이지 - recipeId 사용 */}
          <Route path="/details/:recipeId" element={<RecipeDetailPage />} />
          <Route path="/search" element={<SearchResultPage />} />
          <Route path="/recipes/:type" element={<RecipeListPage />} />
          
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/recipe/write" element={<RecipeWritePage />} />
        </Routes>
      </main>
    </>
  );
}

export default App;