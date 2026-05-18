package lota.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lota.project.dto.MessageDtos;
import lota.project.service.DeliveryService;
import lota.project.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Handles inbound STOMP messages from WebSocket clients.
 *
 * Clients publish to /app/chat.send   → routed here
 *                    /app/chat.typing → typing indicator
 *
 * SRP: only handles WebSocket message routing, delegates all work to services.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MessageService messageService;
    private final DeliveryService deliveryService;

    /**
     * Client sends:  { senderId, receiverId, content, type }
     * Flow: persist → deliver to recipient via STOMP
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDtos.SendRequest request) {
        validateSendRequest(request);
        log.info("Inbound msg: {} → {}", request.getSenderId(), request.getReceiverId());

        MessageDtos.MessageResponse saved = messageService.save(request);
        deliveryService.deliver(saved);
    }

    /**
     * Client sends:  { senderId, receiverId, type: "TYPING" }
     * Broadcasts a typing indicator to the recipient — not persisted.
     */
    @MessageMapping("/chat.typing")
    public void typingIndicator(@Payload MessageDtos.SendRequest request) {
        if (request.getSenderId() == null || request.getReceiverId() == null) return;
        deliveryService.broadcastEvent(MessageDtos.SystemEvent.builder()
                .eventType("TYPING")
                .userId(request.getSenderId())
                .displayName(request.getSenderId())
                .build());
    }

    private void validateSendRequest(MessageDtos.SendRequest req) {
        if (req.getSenderId() == null || req.getSenderId().isBlank())
            throw new IllegalArgumentException("senderId is required");
        if (req.getReceiverId() == null || req.getReceiverId().isBlank())
            throw new IllegalArgumentException("receiverId is required");
        if (req.getContent() == null || req.getContent().isBlank())
            throw new IllegalArgumentException("content is required");
        if (req.getSenderId().equals(req.getReceiverId()))
            throw new IllegalArgumentException("sender and receiver cannot be the same");
    }
}
