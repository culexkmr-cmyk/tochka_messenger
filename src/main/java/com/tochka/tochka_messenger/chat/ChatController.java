package com.tochka.tochka_messenger.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @MessageMapping("/send")            // клиент отправляет в /app/send
    public void handleMessage(@Payload MessageDTO message, @Header("simpSessionAttributes")Map<String, Object> sessionAttrs){
        String roomId = message.getRoomId();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }
}