package com.tochka.tochka_messenger.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String nickname;
    private String avatarUrl;
}