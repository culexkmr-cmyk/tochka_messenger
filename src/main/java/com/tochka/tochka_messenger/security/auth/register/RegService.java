package com.tochka.tochka_messenger.security.auth.register;

import com.tochka.tochka_messenger.security.auth.Encode;
import com.tochka.tochka_messenger.security.auth.Encoder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Value;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class RegService {
    public HttpStatus validation(String password,String username){
        if (password==null || username==null){
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
    public ResponseEntity<?> register(String password, String username){
        Encode encoder=new Encoder();
        HashMap<String, Object> response=new HashMap<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
