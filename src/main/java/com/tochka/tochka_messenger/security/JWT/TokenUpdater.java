package com.tochka.tochka_messenger.security.JWT;

import com.tochka.tochka_messenger.DB.entities.RefreshToken;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.RefreshTokenRepository;
import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import com.tochka.tochka_messenger.security.auth.Encode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenUpdater implements UpdateToken {

    @Autowired
    private Encode encoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenerateAccessToken accessTokenGenerator;

    @Autowired
    private GenerateRefreshToken refreshTokenGenerator;  // Используем существующий сервис

    @Value("${jwt.refresh.secret}")
    private String refreshSecretBase64;

    private SecretKey getRefreshKey() {
        byte[] keyBytes = Base64.getDecoder().decode(refreshSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims parseRefreshToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getRefreshKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    @Override
    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        Map<String, String> response = new HashMap<>();

        if (refreshToken == null || refreshToken.isEmpty()) {
            response.put("error", "Refresh token is required");
            return response;
        }

        try {
            // 1. Парсим refresh token
            Claims claims = parseRefreshToken(refreshToken);
            if (claims == null) {
                response.put("error", "Invalid refresh token");
                return response;
            }

            // 2. Проверяем срок действия
            if (isTokenExpired(claims)) {
                response.put("error", "Refresh token expired");
                return response;
            }

            // 3. Получаем username из токена
            String username = claims.getSubject();

            // 4. Находим пользователя
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user == null) {
                response.put("error", "User not found");
                return response;
            }

            // 5. Хэшируем полученный refresh token
            String tokenHash = encoder.encode(refreshToken);

            // 6. Ищем токен в БД
            RefreshToken existingToken = refreshTokenRepository
                    .findByTokenHash(tokenHash)
                    .orElse(null);

            if (existingToken == null || !existingToken.getUser().getId().equals(user.getId())) {
                // Токен не найден - возможно попытка повторного использования
                refreshTokenRepository.deleteAllByUser(user);
                response.put("error", "Invalid refresh token - all tokens revoked");
                return response;
            }

            // 7. Проверяем срок действия токена в БД
            if (existingToken.getExpiresAt().isBefore(Instant.now())) {
                refreshTokenRepository.delete(existingToken);
                response.put("error", "Refresh token expired");
                return response;
            }

            // 8. РОТАЦИЯ: удаляем старый refresh token
            refreshTokenRepository.delete(existingToken);

            // 9. Генерируем новые токены, используя существующие сервисы
            Map<String, Object> accessClaims = new HashMap<>();
            accessClaims.put("type", "access");
            accessClaims.put("username", username);

            String newAccessToken = accessTokenGenerator.generateAccessToken(username, accessClaims);
            String newRefreshToken = refreshTokenGenerator.generateRefreshToken(username);  // Используем существующий генератор

            // 10. Сохраняем новый refresh token в БД (хэш)
            RefreshToken newRefreshTokenEntity = new RefreshToken();
            newRefreshTokenEntity.setTokenHash(encoder.encode(newRefreshToken));
            newRefreshTokenEntity.setUser(user);
            newRefreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(7L * 24 * 60 * 60)); // 7 дней

            refreshTokenRepository.save(newRefreshTokenEntity);

            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken);
            return response;

        } catch (Exception e) {
            response.put("error", "Token refresh failed: " + e.getMessage());
            return response;
        }
    }

    @Transactional
    public void revokeAllUserTokens(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            refreshTokenRepository.deleteAllByUser(user);
        });
    }

    @Transactional
    public void revokeToken(String refreshToken) {
        String tokenHash = encoder.encode(refreshToken);
        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }
}