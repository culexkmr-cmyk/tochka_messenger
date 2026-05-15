package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.Media;
import com.tochka.tochka_messenger.DB.entities.User;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class MessageDto {
    private Long messageId;
    private String message;
    private Set<Media> mediaSet;
    private Instant time;
    private Long chatId;
}
