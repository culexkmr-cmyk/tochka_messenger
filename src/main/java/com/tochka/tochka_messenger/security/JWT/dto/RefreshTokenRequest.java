package com.tochka.tochka_messenger.security.JWT.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}