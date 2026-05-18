package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Media;
import com.tochka.tochka_messenger.DB.entities.User;

public interface GetMedia {
    Media getMedia(Long mediaId, User requester);
}
