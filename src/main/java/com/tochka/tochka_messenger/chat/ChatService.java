package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.ChatRepository;
import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;
    public Chat createChat(String chatName, Set<String> participantUsernames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        User creator = (User) authentication.getPrincipal();

        Chat chat = new Chat();
        chat.setName(chatName);
        Set<User> chatUsers = new HashSet<>();
        chatUsers.add(creator);
        if (participantUsernames != null) {
            for (String username : participantUsernames) {
                Optional<User> participantOpt = userRepository.findByUsername(username);
                participantOpt.ifPresent(chatUsers::add);
            }
        }

        chat.setUsers(chatUsers);
        return chatRepository.save(chat);
    }
    public Chat addUserToChat(Long chatId, String usernameToAdd) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        User currentUser = (User) authentication.getPrincipal();

        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat == null) {
            return null;
        }
        if (!chat.getUsers().contains(currentUser)) {
            return null;
        }

        Optional<User> userToAddOpt = userRepository.findByUsername(usernameToAdd);
        if (userToAddOpt.isPresent()) {
            chat.getUsers().add(userToAddOpt.get());
            return chatRepository.save(chat);
        }
        return null;
    }
}