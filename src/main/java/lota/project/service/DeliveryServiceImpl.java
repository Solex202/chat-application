package lota.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lota.project.dto.MessageDtos;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Routes messages over STOMP.
 *
 * IMPORTANT: convertAndSendToUser() is used — NOT convertAndSend() with a
 * manually built "/user/{id}/queue/..." path. Spring's user-destination
 * resolver maps the username to the correct session(s) automatically.
 *
 * SRP  : only responsible for WebSocket delivery; no DB access.
 * DIP  : depends on SimpMessagingTemplate + MessageService interfaces.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService        messageService;

    private static final String USER_QUEUE  = "/queue/messages";
    private static final String EVENT_TOPIC = "/topic/events";

    @Override
    public void deliver(MessageDtos.MessageResponse message) {
        // Push to recipient — they receive it in real time
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(), USER_QUEUE, message);

        // Echo the server-confirmed copy back to the sender as well.
        // This gives the sender the real DB id, timestamp, and delivered flag
        // so the UI doesn't need optimistic rendering at all.
        messagingTemplate.convertAndSendToUser(
                message.getSenderId(), USER_QUEUE, message);

        log.info("Delivered msg #{}: {} -> {}",
                message.getId(), message.getSenderId(), message.getReceiverId());

        messageService.markDelivered(message.getId());
    }

    @Override
    public void broadcastEvent(MessageDtos.SystemEvent event) {
        messagingTemplate.convertAndSend(EVENT_TOPIC, event);
        log.info("Broadcast event {} for user {}", event.getEventType(), event.getUserId());
    }

    @Override
    public void flushPending(String userId) {
        List<MessageDtos.MessageResponse> pending = messageService.getPendingMessages(userId);
        if (pending.isEmpty()) return;
        log.info("Flushing {} pending message(s) to {}", pending.size(), userId);
        pending.forEach(this::deliver);
    }
}
