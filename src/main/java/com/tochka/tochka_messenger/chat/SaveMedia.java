package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.Media;
import com.tochka.tochka_messenger.DB.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SaveMedia {
    Media saveMedia(MultipartFile file, Chat chat, User uploader) throws IOException;
}
