package com.som.deliveryplatform.global.auth.filter;

import com.som.deliveryplatform.global.auth.jwt.JwtProvider;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.som.deliveryplatform.global.auth.model.TokenModel.TOKEN_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromCookie(request);

        if (StringUtils.isNotBlank(token)) {
            try {
                Claims claims = jwtProvider.parseToken(token);

                UserPrincipal principal = UserPrincipal.builder()
                        .email(claims.getSubject())
                        .role(jwtProvider.extractRole(claims))
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("success to JWT authentication - email: {}", principal.getEmail());
            } catch (JwtException e) {
                log.warn("fail to JWT authentication. message: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);

    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> TOKEN_COOKIE_NAME.equalsIgnoreCase(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
