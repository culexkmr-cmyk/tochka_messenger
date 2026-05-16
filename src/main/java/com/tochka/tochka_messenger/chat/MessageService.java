package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.Message;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.ChatRepository;
import com.tochka.tochka_messenger.DB.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    public Message createMessage(MessageDto messageDto) {
        // 1. Получаем аутентифицированного пользователя из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        User authenticatedUser = (User) authentication.getPrincipal();

        // 2. Проверяем существование чата
        Chat chat = chatRepository.findById(messageDto.getChatId())
                .orElse(null);
        if (chat == null) {
            return null;
        }

        // 3. Проверяем, что пользователь является участником чата (замена проверки из DTO)
        if (!chat.getUsers().contains(authenticatedUser)) {
            return null; // Пользователь не имеет права отправлять сообщения в этот чат
        }

        // 4. Создаём и сохраняем сообщение
        Message message = new Message();
        message.setChat(chat);
        message.setUser(authenticatedUser); // Используем аутентифицированного пользователя
        message.setText(messageDto.getMessage());
        message.setCreatedAt(Instant.now());
        message.setMediaList(messageDto.getMediaSet());

        return messageRepository.save(message);
    }

    // Рекомендуется добавить отдельный метод для редактирования с проверкой авторства
    public Message editMessage(Long messageId, String newText, Long chatId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        User authenticatedUser = (User) authentication.getPrincipal();

        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null || !message.getChat().getId().equals(chatId)) {
            return null;
        }

        // Проверка: только автор может редактировать сообщение
        if (!message.getUser().getId().equals(authenticatedUser.getId())) {
            return null;
        }

        message.editMessage(newText); // Использует метод из сущности
        return messageRepository.save(message);
    }
}