package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;
@Transactional
@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;
    public Chat createChat(User creator, String chatName, Set<User> users){
        Chat chat=new Chat();
        users.add(creator);
        chat.setUsers(users);
        chat.setName(chatName);
    return chatRepository.save(chat);
    }
}