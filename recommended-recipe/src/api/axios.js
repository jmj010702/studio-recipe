// src/api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/', 
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 모든 요청에 토큰 자동 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken'); 
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 401 에러 시 자동 로그아웃 처리
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 토큰 만료 또는 인증 실패
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userInfo');
      
      // 로그인 페이지로 리다이렉트 (현재 페이지가 로그인이 아닐 때만)
      if (window.location.pathname !== '/login' && window.location.pathname !== '/auth/login') {
        console.warn('인증이 만료되었습니다. 로그인 페이지로 이동합니다.');
        // React Router를 사용하는 경우 window.location 대신 navigate 사용 권장
        window.location.href = '/login';
      }
    }
    // 500 에러는 그냥 에러를 반환 (페이지 이동 X)
    return Promise.reject(error);
  }
);

// 로그인 상태 확인 헬퍼 함수
export const isAuthenticated = () => {
  return !!localStorage.getItem('accessToken');
};

// 로그인 함수
export const login = (token, userInfo) => {
  localStorage.setItem('accessToken', token);
  if (userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }
};

// 로그아웃 함수
export const logout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userInfo');
  window.location.href = '/login';
};

// 현재 사용자 정보 가져오기
export const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
};

export default api;