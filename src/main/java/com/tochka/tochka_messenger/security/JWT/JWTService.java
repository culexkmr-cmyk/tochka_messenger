package com.tochka.tochka_messenger.security.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt.issuer:tochka-messenger}")
    private String issuer;

    @Value("${jwt.audience:tochka-app}")
    private String audience;

    @Value("${jwt.access.secret}")
    private String accessSecretBase64;

    @Value("${jwt.refresh.secret}")
    private String refreshSecretBase64;

    private static final long ACCESS_EXP = 1000L * 60 * 15; // 15 мин
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7 дней

    private SecretKey getAccessKey() {
        validateSecret(accessSecretBase64);
        byte[] keyBytes = Base64.getDecoder().decode(accessSecretBase64);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Access secret must be at least 256 bits (32 bytes)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshKey() {
        validateSecret(refreshSecretBase64);
        byte[] keyBytes = Base64.getDecoder().decode(refreshSecretBase64);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Refresh secret must be at least 256 bits (32 bytes)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private void validateSecret(String secret) {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalArgumentException("JWT secret cannot be empty");
        }
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ACCESS_EXP);

        return Jwts.builder()
                .claims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getAccessKey())
                .compact();
    }

    public String generateRefreshToken(String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + REFRESH_EXP);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getRefreshKey())
                .compact();
    }

    public Claims validateAndGetClaims(String token, boolean isRefresh) {
        SecretKey key = isRefresh ? getRefreshKey() : getAccessKey();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Clock skew ~60s
        Date exp = claims.getExpiration();
        if (exp != null && exp.getTime() < System.currentTimeMillis() - 60000) {
            throw new JwtException("Token expired");
        }

        return claims;
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = validateAndGetClaims(refreshToken, true);
            String subject = claims.getSubject();
            if (subject == null) {
                throw new IllegalArgumentException("Invalid refresh token: no subject");
            }
            // Здесь взять claims из БД по subject + blacklist check
            return generateAccessToken(subject, Map.of("role", "user"));
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid refresh token: " + e.getMessage());
        }
    }
}