package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
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
                messageDto.getMessageId(),
                messageDto.getMessage(),
                messageDto.getChatId()
        );

        if (savedMessage != null) {
            template.convertAndSend("/topic/room/" + messageDto.getChatId(), savedMessage);
        }
    }
}