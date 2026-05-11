package com.tochka.tochka_messenger.security.JWT;

import java.util.Map;

public interface UpdateToken {
    Map<String,String> refreshToken(String refreshToken);
}
