package com.tochka.tochka_messenger.config;

import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import com.tochka.tochka_messenger.security.JWT.AccessTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.access.secret}")
    private String accessSecretBase64;

    @Value("${jwt.issuer:tochka-messenger}")
    private String issuer;

    @Value("${jwt.audience:tochka-app}")
    private String audience;

    private SecretKey getAccessKey() {
        byte[] keyBytes = Base64.getDecoder().decode(accessSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Извлекаем токен из куки
            List<String> cookies = accessor.getNativeHeader("cookie");
            String accessToken = extractTokenFromCookies(cookies);

            if (StringUtils.hasText(accessToken) && validateToken(accessToken)) {
                String username = extractUsername(accessToken);
                Optional<User> userOpt = userRepository.findByUsername(username);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(auth);
                    return message;
                }
            }
            // Отклоняем соединение при неуспешной аутентификации
            return null;
        }
        return message;
    }

    private String extractTokenFromCookies(List<String> cookies) {
        if (cookies == null) return null;
        for (String cookieHeader : cookies) {
            if (cookieHeader.contains("accessToken=")) {
                String[] parts = cookieHeader.split(";");
                for (String part : parts) {
                    if (part.trim().startsWith("accessToken=")) {
                        return part.trim().substring("accessToken=".length());
                    }
                }
            }
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getAccessKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return issuer.equals(claims.getIssuer()) &&
                    audience.equals(claims.getAudience()) &&
                    claims.getExpiration().after(new java.util.Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getAccessKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}