package com.som.deliveryplatform.global.auth.filter;

import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.global.auth.jwt.JwtProperties;
import com.som.deliveryplatform.global.auth.jwt.JwtProvider;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties jwtProperties;

    private final String testEmail = "test@test.com";
    private final Role testRole = Role.USER;
    private final UserPrincipal userPrincipal = UserPrincipal.builder()
            .email(testEmail)
            .role(testRole)
            .build();

    @Test
    @DisplayName("유효한 JWT 쿠키가 있는 경우 인증 성공")
    void shouldAuthenticateWithValidJwtCookie() throws Exception {
        // given
        String token = jwtProvider.generateJWTToken(userPrincipal);
        MockCookie cookie = new MockCookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // when & then
        mockMvc.perform(get("/api/auth/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(testEmail))
                .andExpect(jsonPath("$.data.role").value(testRole.name()));
    }

    @Test
    @DisplayName("잘못된 JWT 쿠키일 경우 인증 실패")
    void shouldReturn401WhenInvalidCookie() throws Exception {
        // given
        MockCookie cookie = new MockCookie("access_token", "invalid.token.value");
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // when & then
        mockMvc.perform(get("/api/auth/me").cookie(cookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JWT 쿠키 없이 요청시 인증 실패")
    void shouldReturn401WhenNotJwtCookie() throws Exception {
        // when &  then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

}