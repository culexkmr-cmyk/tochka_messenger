package com.tochka.tochka_messenger.security.auth.login;

import com.tochka.tochka_messenger.security.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto request, HttpServletResponse response) {
        LoginResult result = loginService.login(request.getPassword(), request.getUsername());

        if (result.isSuccess()) {
            response.addHeader("Set-Cookie", cookieUtil.createAccessTokenCookie(result.getAccessToken()).toString());
            response.addHeader("Set-Cookie", cookieUtil.createRefreshTokenCookie(result.getRefreshToken()).toString());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(result.getStatusCode()).build();
        }
    }
}