package com.som.deliveryplatform.global.auth.service;

import com.som.deliveryplatform.domain.user.entity.User;
import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.domain.user.repository.UserRepository;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        userRequest.getClientRegistration().getRegistrationId();  // google, naver, kakao (차후 연동할 경우 분리)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. Provider(google)로부터 받은 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. DB에서 사용자 존재 여부 확인 (존재하지 않다면 Guset 권한을 주어 반환한다)
        String email = (String) attributes.get("email");    // google 경우 email을 그냥 꺼낼 수 있다
        Optional<User> optionalUser = userRepository.findByEmail(email);

        boolean userPresent = optionalUser.isPresent();
        Role role = userPresent ? optionalUser.get().getRole(): Role.GUEST;
        Long id = userPresent ? optionalUser.get().getId() : null;

        // 3. 서버 내부 표준 사용자(UserPrincipal)로 변환
        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .role(role)
                .attributes(attributes)
                .build();
    }
}
