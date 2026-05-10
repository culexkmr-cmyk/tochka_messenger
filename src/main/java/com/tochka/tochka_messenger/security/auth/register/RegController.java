package com.tochka.tochka_messenger.security.auth.register;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegController {
    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody RegDTO regDTO){
        RegService regService=new RegService();
        return regService.register(regDTO.getPassword(), regDTO.getUsername());
    }
}
