package com.som.deliveryplatform.global.auth.service;

import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        userRequest.getClientRegistration().getRegistrationId();  // google, naver, kakao

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. Provider로부터 받은 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. 필요에 따라 attributes 가공
        String email = (String) attributes.get("email");    // google 경우
        String role = "ROLE_USER";  // 기본적으로 일반 사용자

        // 3. 서버 내부 표준 사용자(UserPrincipal)로 변환
        return UserPrincipal.builder()
                .email(email)
                .role(role)
                .attributes(attributes)
                .build();
    }
}
