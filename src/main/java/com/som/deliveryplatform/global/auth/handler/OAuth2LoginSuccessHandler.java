package com.som.deliveryplatform.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.global.auth.dto.LoginSuccessResponse;
import com.som.deliveryplatform.global.auth.jwt.JwtProperties;
import com.som.deliveryplatform.global.auth.jwt.JwtProvider;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.som.deliveryplatform.global.auth.model.TokenModel.TOKEN_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // JWT 토큰 생성
        String token = jwtProvider.generateJWTToken(principal);

        // JWT 토큰 쿠키에 저장 (HttpOnly)
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.getExpirationSeconds());

        response.addCookie(cookie);

        Role role = principal.getRole();
        log.info("success login: {}, role: {}", principal.getName(), role);

        // 로그인 후 Json 응답 처리
        LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse(principal.getName(), role);
        ResponseDto<LoginSuccessResponse> responseBody = ResponseDto.of(ResponseCode.SUCCESS, loginSuccessResponse);

        response.setContentType(ContentType.APPLICATION_JSON.getType());
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
