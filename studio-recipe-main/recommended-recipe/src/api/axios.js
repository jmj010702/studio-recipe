// src/api/axios.js
import axios from 'axios';

// Vite 프록시 설정을 사용할 경우
const api = axios.create({
  baseURL: '/', // 모든 요청을 현재 서버(Vite)로 보냄 (Vite가 백엔드로 프록시)
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

/*
// .env 파일을 사용할 경우 (Vite 프록시 안 쓸 때)
const api = axios.create({
  baseURL: import.meta.env.VITE_APP_SERVER_URL, // 예: http://localhost:8080
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});
*/

export default api;