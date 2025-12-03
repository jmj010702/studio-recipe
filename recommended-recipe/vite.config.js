import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      
      // 1. /api 경로 (모든 API 요청)
      '/api': { 
        target: 'http://localhost:8080',
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