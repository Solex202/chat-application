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
 * Each user subscribes to /user/{userId}/queue/messages  (private)
 * and /topic/events                                       (broadcasts)
 *
 * SRP  : only responsible for WebSocket delivery; no DB access.
 * DIP  : depends on SimpMessagingTemplate + MessageService interfaces.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    /** STOMP-over-WebSocket template injected by Spring. */
    private final SimpMessagingTemplate  messagingTemplate;
    private final MessageService         messageService;

    private static final String USER_QUEUE   = "/queue/messages";
    private static final String EVENT_TOPIC  = "/topic/events";

    @Override
    public void deliver(MessageDtos.MessageResponse message) {
        String destination = "/user/" + message.getReceiverId() + USER_QUEUE;
        messagingTemplate.convertAndSend(destination, message);
        log.info("Delivered msg #{} to {} via {}", message.getId(),
                message.getReceiverId(), destination);
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
