package com.tochka.tochka_messenger.security.auth.login;

import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import com.tochka.tochka_messenger.security.JWT.AccessTokenGenerator;
import com.tochka.tochka_messenger.security.JWT.RefreshTokenGenerator;
import com.tochka.tochka_messenger.security.auth.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    Encoder encoder;
    @Autowired
    RefreshTokenGenerator refreshTokenGenerator;
    @Autowired
    AccessTokenGenerator accessTokenGenerator;
    public ResponseEntity<?> login(String password, String username){
        if (userRepository.findByUsername(username).isPresent()){
            if (encoder.match(password, userRepository.findByUsername(username).get().getPassword())){
                return ResponseEntity.status(HttpStatus.OK).
                        body(Map.of("refreshToken", refreshTokenGenerator.generateRefreshToken(username),
                                "accessToken",accessTokenGenerator.generateAccessToken(username, Map.of("type","access","username", username ))));
            }
            else {return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();}
        }
        else {return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
    }
}
