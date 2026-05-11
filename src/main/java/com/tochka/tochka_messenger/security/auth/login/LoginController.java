package com.tochka.tochka_messenger.security.auth.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginDto request){
        return loginService.login(request.getPassword(), request.getPassword());
    }
}
