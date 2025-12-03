// src/page/App.jsx
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from '../components/Header.jsx';
import MainPage from './MainPage.jsx';
import SignupPage from './SignupPage.jsx';
import LoginPage from './LoginPage.jsx';
import MyPage from './MyPage.jsx';
import RecipeWritePage from './RecipeWritePage.jsx';
import RecipeDetailPage from './RecipeDetailpage.jsx';
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
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/recipe/write" element={<RecipeWritePage />} />
          <Route path="/recipe/:recipeId" element={<RecipeDetailPage />} />
        </Routes>
      </main>
    </>
  );
}

export default App;
