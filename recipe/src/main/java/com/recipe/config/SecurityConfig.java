package com.recipe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Value("${front.url}")
    private String frontUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of(frontUrl));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization", "Refresh-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(authorize ->
                        authorize
                                       .requestMatchers("/batch/run-recipe-csv").hasRole("ADMIN")
                                // ✅ 인증 필요한 엔드포인트 (먼저 선언)
                                .requestMatchers("/api/details/likes").authenticated()
                                .requestMatchers("/api/details/completion").authenticated()
                                
                                // ✅ 인증 불필요한 엔드포인트
                                .requestMatchers(
                                    // 메인 페이지 관련
                                    "/",
                                    "/api/mainPages",
                                    "/api/recipes/**",
                                    "/api/details/**",      // ✅ 추가! (레시피 상세 조회는 누구나 가능)
                                    "/api/admin/**",
                                    
                                    // 인증 관련
                                    "/auth/**",
                                    
                                    // Batch 관련
                                    "/batch/run-recipe-csv",
                                    
                                    // Swagger 문서
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/v3/api-docs",
                                    
                                    // 기타
                                    "/error",
                                    "/test/**",
                                        "/actuator/**",
                                  "/aws-test/**"
                                  
                                ).permitAll()
                                // 나머지 모든 요청은 인증된 사용자만 허용
                                .anyRequest().authenticated()
                )
                
                // JWT 필터 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
