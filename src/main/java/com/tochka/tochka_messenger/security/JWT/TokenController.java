package com.tochka.tochka_messenger.security.JWT.controller;

import com.tochka.tochka_messenger.security.JWT.TokenUpdater;
import com.tochka.tochka_messenger.security.JWT.dto.RefreshTokenRequest;
import com.tochka.tochka_messenger.security.JWT.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    @Autowired
    private TokenUpdater tokenUpdater;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        Map<String, String> result = tokenUpdater.refreshToken(request.getRefreshToken());

        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(new TokenResponse(
                result.get("accessToken"),
                result.get("refreshToken")
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        tokenUpdater.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}