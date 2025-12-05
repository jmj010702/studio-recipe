import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
<<<<<<< HEAD
      '/api': { //api로 시작하는 요청은 모두 여기로 전달
        target: 'http://recipe-app-alb-643440183.ap-northeast-2.elb.amazonaws.com/studio-recipe', // Spring Boot 서버 주소를 여기로 지정
=======
      
      // 1. /api 경로 (모든 API 요청)
      '/api': { 
        target: 'http://localhost:8080',
>>>>>>> bfe4f1237b34f8a6742385b0a168ca9cac5ed80a
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/studio-recipe/api') 
      },

      // 2. /auth 경로 (로그인, 회원가입, 비밀번호찾기 등)
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/auth/, '/studio-recipe/auth')
      },

      // 3. /user 경로 추가 (회원 탈퇴, 비밀번호 변경 등)
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/user/, '/studio-recipe/user')
      },

      // 4. /images 경로 (정적 이미지 리소스)
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/images/, '/studio-recipe/images')
      }
    }
  }
});