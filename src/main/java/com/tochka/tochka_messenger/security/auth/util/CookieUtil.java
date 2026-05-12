package com.tochka.tochka_messenger.security.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.access-token.max-age:900}")
    private long accessTokenMaxAge;

    @Value("${app.refresh-token.max-age:604800}")
    private long refreshTokenMaxAge;

    public ResponseCookie createAccessTokenCookie(String token) {
        return createCookie("accessToken", token, accessTokenMaxAge, "/api");
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return createCookie("refreshToken", token, refreshTokenMaxAge, "/api/auth");
    }

    public ResponseCookie createClearAccessTokenCookie() {
        return createClearCookie("accessToken", "/api");
    }

    public ResponseCookie createClearRefreshTokenCookie() {
        return createClearCookie("refreshToken", "/api/auth");
    }

    private ResponseCookie createCookie(String name, String value, long maxAge, String path) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .maxAge(maxAge)
                .sameSite("Strict");

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }

    private ResponseCookie createClearCookie(String name, String path) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(path)
                .maxAge(0)
                .sameSite("Strict");

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }
}