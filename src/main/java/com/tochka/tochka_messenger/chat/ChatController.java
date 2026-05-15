package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequest request) {
        try {
            Chat chat = chatService.createChat(request.getChatName(), request.getParticipantUsernames());
            if (chat == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated or invalid session"));
            }
            return ResponseEntity.ok(Map.of(
                    "id", chat.getId(),
                    "name", chat.getName(),
                    "message", "Chat created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create chat: " + e.getMessage()));
        }
    }

    @PostMapping("/{chatId}/add-user")
    public ResponseEntity<?> addUserToChat(@PathVariable Long chatId, @RequestBody AddUserRequest request) {
        try {
            Chat updatedChat = chatService.addUserToChat(chatId, request.getUsername());
            if (updatedChat == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Chat not found or you don't have permission to add users"));
            }
            return ResponseEntity.ok(Map.of(
                    "chatId", updatedChat.getId(),
                    "chatName", updatedChat.getName(),
                    "message", "User added successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add user: " + e.getMessage()));
        }
    }
    @PutMapping("/create")
    public ResponseEntity<?> updateChatName(@RequestBody UpdateChatNameRequest request) {
        try {
            Chat chat = chatService.updateChatName(request.getChatId(), request.getNewChatName());
            if (chat == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated or invalid session"));
            }
            return ResponseEntity.ok(Map.of(
                    "id", chat.getId(),
                    "name", chat.getName(),
                    "message", "Chat created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update chatName: " + e.getMessage()));
        }
    }
}

@Data
class CreateChatRequest {
    private Long chatId;
    private String chatName;
    private Set<String> participantUsernames;
}
@Data
class UpdateChatNameRequest {
    private Long chatId;
    private String newChatName;
}

@Data
class AddUserRequest {
    private Long chatId;
    private String username;

}