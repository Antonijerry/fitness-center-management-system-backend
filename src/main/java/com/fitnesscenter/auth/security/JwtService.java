package com.fitnesscenter.auth.security;

import com.fitnesscenter.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    public String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .toList()
        );

        return generateToken(
                claims,
                user.getEmail(),
                jwtProperties.accessTokenExpiration()
        );
    }


    public String generateRefreshToken(User user) {

        return generateToken(
                Map.of(),
                user.getEmail(),
                jwtProperties.refreshTokenExpiration()
        );
    }


    private String generateToken(
            Map<String, Object> claims,
            String subject,
            long expiration
    ) {

        Date issuedAt = new Date();

        Date expirationDate =
                new Date(
                        issuedAt.getTime() + expiration
                );

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }


    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }


    public boolean isTokenValid(
            String token,
            String username
    ) {

        Claims claims = extractAllClaims(token);

        return claims.getSubject().equals(username)
                && !isTokenExpired(claims);
    }


    private boolean isTokenExpired(Claims claims) {

        return claims
                .getExpiration()
                .before(new Date());
    }


    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        jwtProperties.secret()
                );

        return Keys.hmacShaKeyFor(keyBytes);
    }
}