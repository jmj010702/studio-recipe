import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': { //api로 시작하는 요청은 모두 여기로 전달
        target: 'http://recipe-app-alb-643440183.ap-northeast-2.elb.amazonaws.com/studio-recipe', // Spring Boot 서버 주소를 여기로 지정
        changeOrigin: true,
      }
    }
  }
})