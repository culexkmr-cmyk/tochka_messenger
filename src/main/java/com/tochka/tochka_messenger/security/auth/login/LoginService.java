package com.tochka.tochka_messenger.security.auth.login;

import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import com.tochka.tochka_messenger.security.JWT.AccessTokenGenerator;
import com.tochka.tochka_messenger.security.JWT.RefreshTokenGenerator;
import com.tochka.tochka_messenger.security.auth.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Encoder encoder;

    @Autowired
    private RefreshTokenGenerator refreshTokenGenerator;

    @Autowired
    private AccessTokenGenerator accessTokenGenerator;

    public LoginResult login(String password, String username) {
        return userRepository.findByUsername(username)
                .filter(user -> encoder.match(password, user.getPassword()))
                .map(user -> {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("type", "access");
                    claims.put("username", username);

                    String accessToken = accessTokenGenerator.generateAccessToken(username, claims);
                    String refreshToken = refreshTokenGenerator.generateRefreshToken(username);

                    return LoginResult.success(accessToken, refreshToken);
                })
                .orElse(LoginResult.failure(HttpStatus.UNAUTHORIZED));
    }
}