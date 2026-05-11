package com.tochka.tochka_messenger.security.auth.register;

import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import com.tochka.tochka_messenger.security.auth.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegService {

    @Autowired
    private Encode encoder;

    @Autowired
    private UserRepository userRepository;

    public HttpStatus validation(String password, String username) {
        if (password == null || username == null || password.isEmpty() || username.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }
        if (username.length() < 3 || username.length() > 50) {
            return HttpStatus.BAD_REQUEST;
        }
        if (password.length() < 6) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    @Transactional
    public ResponseEntity<?> register(String password, String username) {
        Map<String, Object> response = new HashMap<>();

        // Валидация
        HttpStatus validationStatus = validation(password, username);
        if (validationStatus != HttpStatus.OK) {
            response.put("error", "Invalid username or password");
            response.put("requirements", "Username: 3-50 chars, Password: min 6 chars");
            return ResponseEntity.status(validationStatus).body(response);
        }

        // Проверка существования пользователя
        if (userRepository.findByUsername(username).isPresent()) {
            response.put("error", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {

            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            response.put("message", "Registration successful");
            response.put("username", username);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}