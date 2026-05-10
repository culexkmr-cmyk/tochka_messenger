package com.tochka.tochka_messenger.security.auth.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
public class RegDTO {
    @Getter@Setter
    private final String password;
    @Getter@Setter
    private final String username;
}
