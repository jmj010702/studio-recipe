package com.recipe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ⭐ 경로 끝에 반드시 슬래시(/) 필요!
        String imageLocation = "file:///C:/project/studio-recipe/recipe/uploads/images/";
        
        System.out.println("========================================");
        System.out.println("WebConfig 정적 리소스 설정:");
        System.out.println("Handler: /images/**");
        System.out.println("Location: " + imageLocation);
        System.out.println("========================================");
        
        // ⭐ 기존 핸들러보다 먼저 등록되도록 설정
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations(imageLocation)
            .setCachePeriod(0);  // ⭐ 개발 중에는 캐시 비활성화
    }
    
    // ⭐ CORS 설정 (SecurityConfig와 중복될 수 있으므로 확인)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}