package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/sendmessage")
    public void sendMessage(@Payload MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return; // Отклоняем неавторизованные запросы
        }

        Message savedMessage = messageService.createMessage(messageDto);
        if (savedMessage != null) {
            template.convertAndSend("/topic/room/" + messageDto.getChatId(), savedMessage);
        }
    }

    @MessageMapping("/chat/editMessage")
    public void editMessage(@Payload MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        // Для редактирования используем отдельный метод сервиса с проверкой авторства
        Message savedMessage = messageService.editMessage(
                messageDto.getMessageId(), // Предполагается, что DTO содержит ID сообщения
                messageDto.getMessage(),
                messageDto.getChatId()
        );

        if (savedMessage != null) {
            template.convertAndSend("/topic/room/" + messageDto.getChatId(), savedMessage);
        }
    }
}