package com.tochka.tochka_messenger.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Encoder implements Encode{
    @Autowired
    private PasswordEncoder passwordEncoder;
    public String encode(String password){
        return passwordEncoder.encode(password);
    }
    public Boolean match(String password, String hash){
        return passwordEncoder.matches(password,hash);
    }
}
