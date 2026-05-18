package lota.project.controller;

import lombok.RequiredArgsConstructor;
import lota.project.dto.MessageDtos;
import lota.project.service.MessageService;
import lota.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints used by the UI on page load (history, user list).
 *
 * SRP: REST only — no WebSocket logic here.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final MessageService messageService;
    private final UserService userService;

    /* ── Users ─────────────────────────────────────────────────────────── */

    @GetMapping("/users")
    public ResponseEntity<List<MessageDtos.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/online")
    public ResponseEntity<List<MessageDtos.UserResponse>> getOnlineUsers() {
        return ResponseEntity.ok(userService.getOnlineUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<MessageDtos.UserResponse> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /* ── Messages ───────────────────────────────────────────────────────── */

    @GetMapping("/messages/{userA}/{userB}")
    public ResponseEntity<MessageDtos.ConversationResponse> getConversation(
            @PathVariable String userA,
            @PathVariable String userB) {
        return ResponseEntity.ok(messageService.getConversation(userA, userB));
    }

    @GetMapping("/messages/{userA}/{userB}/recent")
    public ResponseEntity<MessageDtos.ConversationResponse> getRecentMessages(
            @PathVariable String userA,
            @PathVariable String userB,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(messageService.getRecentMessages(userA, userB, limit));
    }

    @GetMapping("/messages/{userId}/pending")
    public ResponseEntity<List<MessageDtos.MessageResponse>> getPending(
            @PathVariable String userId) {
        return ResponseEntity.ok(messageService.getPendingMessages(userId));
    }
}
