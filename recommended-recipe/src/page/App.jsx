// src/page/App.jsx
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
import RecipeListPage from "./RecipeListPage";
import FindPasswordPage from './FindPasswordPage.jsx';

// ▼▼▼ [핵심] ProtectedRoute import 구문 "삭제" ▼▼▼
// import ProtectedRoute from '../components/ProtectedRoute.jsx';

import './App.css'; 

function App() {
  return (
    <> 
      <Header />
      <main className="main-content">
        <Routes>
          {/* --- 모든 페이지를 일반 라우트로 변경 --- */}
          <Route path="/" element={<MainPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/find-id" element={<FindIdPage />} />
          <Route path="/find-password" element={<FindPasswordPage />} />
          <Route path="/recipe/:recipeId" element={<RecipeDetailPage />} />
          <Route path="/recipes/:type" element={<RecipeListPage />} />
          
          {/* ▼▼▼ [핵심] ProtectedRoute 래퍼 "제거" ▼▼▼ */}
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/recipe/write" element={<RecipeWritePage />} />
          {/* ▲▲▲ [핵심] ProtectedRoute 래퍼 "제거" ▲▲▲ */}

        </Routes>
      </main>
    </>
  );
}

export default App;