package com.tochka.tochka_messenger.security.JWT.controller;

import com.tochka.tochka_messenger.security.JWT.TokenUpdater;
import com.tochka.tochka_messenger.security.JWT.dto.RefreshTokenRequest;
import com.tochka.tochka_messenger.security.auth.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    @Autowired
    private TokenUpdater tokenUpdater;

    @Autowired
    private CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody(required = false) RefreshTokenRequest request,
                                          HttpServletRequest httpRequest,
                                          HttpServletResponse response) {
        String refreshToken = Optional.ofNullable(request)
                .map(RefreshTokenRequest::getRefreshToken)
                .filter(token -> !token.isEmpty())
                .orElseGet(() -> extractCookieValue("refreshToken", httpRequest));

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
        }

        Map<String, String> result = tokenUpdater.refreshToken(refreshToken);

        if (result.containsKey("error")) {
            response.addHeader("Set-Cookie", cookieUtil.createClearAccessTokenCookie().toString());
            response.addHeader("Set-Cookie", cookieUtil.createClearRefreshTokenCookie().toString());
            return ResponseEntity.badRequest().body(result);
        }

        response.addHeader("Set-Cookie", cookieUtil.createAccessTokenCookie(result.get("accessToken")).toString());
        response.addHeader("Set-Cookie", cookieUtil.createRefreshTokenCookie(result.get("refreshToken")).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) RefreshTokenRequest request,
                                    HttpServletRequest httpRequest,
                                    HttpServletResponse response) {
        String refreshToken = Optional.ofNullable(request)
                .map(RefreshTokenRequest::getRefreshToken)
                .filter(token -> !token.isEmpty())
                .orElseGet(() -> extractCookieValue("refreshToken", httpRequest));

        if (refreshToken != null && !refreshToken.isEmpty()) {
            tokenUpdater.revokeToken(refreshToken);
        }

        response.addHeader("Set-Cookie", cookieUtil.createClearAccessTokenCookie().toString());
        response.addHeader("Set-Cookie", cookieUtil.createClearRefreshTokenCookie().toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private String extractCookieValue(String name, HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}