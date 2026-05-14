package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequest request) {
        Chat chat = chatService.createChat(request.getChatName(), request.getParticipants());
        if (chat != null) {
            return ResponseEntity.ok(chat);
        }
        return ResponseEntity.status(403).body("Failed to create chat: unauthorized or invalid participants");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<Chat>> getUserChats() {
        Set<Chat> chats = chatService.getUserChats();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        if (chat != null) {
            return ResponseEntity.ok(chat);
        }
        return ResponseEntity.status(404).body("Chat not found or access denied");
    }
    @PostMapping("/{chatId}/users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addUserToChat(@PathVariable Long chatId,
                                           @RequestBody AddUserRequest request) {
        Chat chat = chatService.addUserToChat(chatId, request.getUsername());
        if (chat != null) {
            return ResponseEntity.ok(chat);
        }
        return ResponseEntity.status(403).body("Failed to add user: unauthorized or user not found");
    }

    @lombok.Data
    public static class CreateChatRequest {
        private String chatName;
        private Set<String> participants;
    }

    @lombok.Data
    public static class AddUserRequest {
        private String username;
    }
}