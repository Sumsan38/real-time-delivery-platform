package com.som.deliveryplatform.support;

import com.som.deliveryplatform.domain.user.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTestHelper {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key securityKey;

    @PostConstruct
    public void init() {
        this.securityKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createTestToken(String userEmail, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userEmail)
                .claim("role", role.getKey())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(securityKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
