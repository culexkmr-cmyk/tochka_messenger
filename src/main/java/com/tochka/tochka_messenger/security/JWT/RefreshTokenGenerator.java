package com.tochka.tochka_messenger.security.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
@Service
public class RefreshTokenGenerator implements GenerateRefreshToken{
    @Value("${jwt.issuer:tochka-messenger}") private String issuer;
    @Value("${jwt.audience:tochka-app}") private String audience;
    @Value("${jwt.refresh.secret}") private String refreshSecretBase64;
    private static final long REFRESH_EXP = 1000L * 60 * 60 * 24 * 7; // 7 дней

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
}

