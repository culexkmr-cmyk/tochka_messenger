package com.tochka.tochka_messenger.security.JWT;

public interface GenerateRefreshToken {
    String generateRefreshToken(String subject);
}
