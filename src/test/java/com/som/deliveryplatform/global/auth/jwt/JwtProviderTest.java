package com.som.deliveryplatform.global.auth.jwt;

import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private final String testEmail = "test@test.com";
    private final Role testRole = Role.USER;
    private final JwtProperties jwtProperties =
            new JwtProperties("my-test-secret-key-my-test-secret-key", 3600000L); // 1시간
    private final JwtProvider jwtProvider = new JwtProvider(jwtProperties);
    private final UserPrincipal userPrincipal = UserPrincipal.builder()
            .email(testEmail)
            .role(testRole)
            .build();

    @Test
    @DisplayName("JWT 토큰 생성 밎 파싱 성공")
    void generateAndParserToken() {
        // when
        String token = jwtProvider.generateJWTToken(userPrincipal);
        Claims claims = jwtProvider.parseToken(token);
        Role role = jwtProvider.extractRole(claims);

        // then
        assertThat(role).isEqualTo(testRole);
        assertThat(claims.getSubject()).isEqualTo(testEmail);
        assertThat(claims).containsEntry("role", testRole.getKey());
    }

    
    @Test
    @DisplayName("잘못된 token parsing 시도 시 예외 발생")
    void generateAndParserToken_fail() {
        // given
        String invalidToken = "IsInvalidToken_parsingTest_throwException";

        // when & then
        assertThatThrownBy(() -> jwtProvider.parseToken(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    
    @Test
    @DisplayName("만료 token parsing 시도 시 예외 발생")
    void generateAndParserToken_fail2() {
        // given
        Date now = new Date();
        Date exprieDate = new Date(now.getTime() - 1000);
        Key securityKey = jwtProvider.getSecurityKey();

        String token = Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("role", userPrincipal.getRole().getKey())
                .setIssuedAt(now)
                .setExpiration(exprieDate)
                .signWith(securityKey, SignatureAlgorithm.HS256)
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtProvider.parseToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
    
    
    
    





    

}