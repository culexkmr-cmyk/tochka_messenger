package com.tochka.tochka_messenger.security.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class LoginResult {
    private final boolean success;
    private final String accessToken;
    private final String refreshToken;
    private final int statusCode;

    public static LoginResult success(String accessToken, String refreshToken) {
        return new LoginResult(true, accessToken, refreshToken, HttpStatus.OK.value());
    }

    public static LoginResult failure(HttpStatus status) {
        return new LoginResult(false, null, null, status.value());
    }

    public static LoginResult failure(int statusCode) {
        return new LoginResult(false, null, null, statusCode);
    }
}