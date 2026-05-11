package com.tochka.tochka_messenger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {
    @Value("${security.salt.length:16}") private Integer saltLength;
    @Value("${security.argon2.iteration:3}") private Integer iteration;
    @Value("${security.argon2.hashLength:32}") private Integer hashLength;
    @Value("${security.argon2.parallelism:1}") private Integer parallelism;
    @Value("${security.argon2.memory:65536}") private Integer memory;  // Исправлено: добавлена закрывающая скобка
    @Value("${security.bcrypt.strength:12}") private Integer strength;
    @Value("${security.scrypt.cpuCost:13}") private int cpuCost;
    @Value("${security.scrypt.memoryCost:13}") private int memoryCost;
    @Value("${security.scrypt.parallelisation:1}") private int parallelization;
    @Value("${security.scrypt.keyLength:32}") private int keyLength;
    @Value("${security.password.encoder.type:bcrypt}") private String encoderType;

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        encoders.put("bcrypt", new BCryptPasswordEncoder(strength));
        encoders.put("argon2", new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iteration));
        encoders.put("scrypt", new SCryptPasswordEncoder(cpuCost, memoryCost, parallelization, keyLength, saltLength));

        return new DelegatingPasswordEncoder(encoderType, encoders);
    }
}