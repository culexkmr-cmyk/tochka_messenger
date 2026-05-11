package com.tochka.tochka_messenger.security.auth.login;

import lombok.Data;

@Data
public class LoginDto {
    private String password;
    private String username;
}
