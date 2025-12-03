// src/api/axios.js
import axios from 'axios';

const api = axios.create({
<<<<<<< HEAD
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  withCredentials: true,
=======
  // ✅ baseURL 제거 - Vite 프록시 사용
  timeout: 10000,
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
  headers: {
    'Content-Type': 'application/json',
  },
});

<<<<<<< HEAD
export default api;
=======
// 요청 인터셉터: 모든 요청에 토큰 자동 추가 및 로깅
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken'); 
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    console.log('🚀 API 요청:', config.method.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('❌ 요청 오류:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 응답 처리 및 에러 핸들링
api.interceptors.response.use(
  (response) => {
    console.log('✅ API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('❌ 응답 오류:', error.response?.status, error.config?.url);
    
    // 401 에러: 인증 실패 - 로그인 페이지로 리다이렉트
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userInfo');
      
      if (window.location.pathname !== '/login') {
        console.warn('⚠️ 인증이 만료되었습니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/login';
      }
    }
    
    return Promise.reject(error);
  }
);

// 로그인 상태 확인
export const isAuthenticated = () => {
  return !!localStorage.getItem('accessToken');
};

// 로그인 처리
export const login = (token, userInfo) => {
  localStorage.setItem('accessToken', token);
  if (userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }
  console.log('✅ 로그인 성공');
};

// 로그아웃 처리
export const logout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userInfo');
  console.log('✅ 로그아웃 완료');
  window.location.href = '/login';
};

// 현재 사용자 정보 가져오기
export const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
};

export default api;
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
