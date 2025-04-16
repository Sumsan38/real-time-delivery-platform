package com.som.deliveryplatform.global.auth.jwt;

import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    @Getter
    private final Key securityKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.securityKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateJWTToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("role", userPrincipal.getRole().getKey())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(securityKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(securityKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Role extractRole(Claims claims) {
        String roleKey = claims.get("role", String.class);
        return Role.valueOfKey(roleKey);
    }
}
