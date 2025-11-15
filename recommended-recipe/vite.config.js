import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      
      // 1. /api 경로 (MainPage 검색, RecipeWritePage 등)
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

      // 3. /mypages 경로 (마이페이지 데이터)
      '/mypages': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/mypages/, '/studio-recipe/mypages') 
      },

      // 4. /mainPages 경로 (메인페이지 데이터)
      '/mainPages': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/mainPages/, '/studio-recipe/mainPages') 
      },
      
      // 5. /details 경로 (레시피 상세페이지 데이터)
      '/details': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/details/, '/studio-recipe/details') 
      }
    }
  }
}); 