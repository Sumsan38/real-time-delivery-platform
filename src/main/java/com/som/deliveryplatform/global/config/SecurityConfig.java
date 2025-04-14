package com.som.deliveryplatform.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)          // csrf 비활성화
                .formLogin(AbstractHttpConfigurer::disable)     // form login 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)     // http basic 인증 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()       // 루트 경로 허용
                        .requestMatchers("/api/public/**").permitAll()  // 퍼블릭 API 허용
                        .anyRequest().authenticated()           // 그 외 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2   // OAuth2 로그인 설정
                        .defaultSuccessUrl("/")                 // 로그인 성공시 이동 URL
                );

        return http.build();
    }
}
