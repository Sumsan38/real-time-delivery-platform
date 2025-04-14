package com.som.deliveryplatform.global.config;

import com.som.deliveryplatform.global.auth.handler.OAuth2LoginFailureHandler;
import com.som.deliveryplatform.global.auth.handler.OAuth2LoginSuccessHandler;
import com.som.deliveryplatform.global.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

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
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))      // OAuth2 로그인 완료 후 AccessToken으로 사용자 정보를 조회하는 서비스 지정
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        return http.build();
    }
}
