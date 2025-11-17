// src/page/App.jsx
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from '../components/Header.jsx';
import MainPage from './MainPage.jsx';
import SignupPage from './SignupPage.jsx';
import LoginPage from './LoginPage.jsx'; 
import MyPage from './MyPage.jsx';
import RecipeWritePage from './RecipeWritePage.jsx';
// 1. (신규) RecipeDetailPage 컴포넌트를 import 합니다.
import RecipeDetailPage from './RecipeDetailPage.jsx';

import './App.css'; 

function App() {
  return (
    <> 
      <Header />
      <main className="main-content">
        <Routes>
          {                     /* 기존 라우트들 */}
          <Route path="/" element={<MainPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/recipe/write" element={<RecipeWritePage />} />
          
          {}
          {      }
          <Route path="/recipe/:recipeId" element={<RecipeDetailPage />} />

        </Routes>
      </main>
    </>
  );
}

export default App;