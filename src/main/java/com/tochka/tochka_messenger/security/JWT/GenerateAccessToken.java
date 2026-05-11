package com.tochka.tochka_messenger.security.JWT;

import java.util.Map;

public interface GenerateAccessToken {
    String generateAccessToken(String subject, Map<String, Object> claims);
}
