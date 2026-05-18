package lota.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lota.project.dto.MessageDtos;
import lota.project.service.DeliveryService;
import lota.project.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listens to Spring WebSocket lifecycle events.
 *
 * SRP  : only manages connection/disconnection logic.
 * OCP  : new events can be added without modifying existing listeners.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventHandler {

    private final UserService userService;
    private final DeliveryService deliveryService;

    /** sessionId → userId mapping so we can look up who disconnected. */
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId      = getHeader(accessor, "userId");
        String displayName = getHeader(accessor, "displayName");
        String sessionId   = accessor.getSessionId();

        if (userId == null || userId.isBlank()) {
            log.warn("WS connect without userId header (session={})", sessionId);
            return;
        }

        sessionUserMap.put(sessionId, userId);
        log.info("WS connected: {} (session={})", userId, sessionId);

        userService.registerOrGet(MessageDtos.UserRequest.builder()
                .userId(userId)
                .displayName(displayName != null ? displayName : userId)
                .build());

        deliveryService.broadcastEvent(MessageDtos.SystemEvent.builder()
                .eventType("USER_JOINED")
                .userId(userId)
                .displayName(displayName != null ? displayName : userId)
                .timestamp(Instant.now())
                .build());

        // flush any messages that arrived while offline
        deliveryService.flushPending(userId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userId    = sessionUserMap.remove(sessionId);

        if (userId == null) return;
        log.info("WS disconnected: {} (session={})", userId, sessionId);

        userService.setOnline(userId, false);

        deliveryService.broadcastEvent(MessageDtos.SystemEvent.builder()
                .eventType("USER_LEFT")
                .userId(userId)
                .timestamp(Instant.now())
                .build());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String getHeader(StompHeaderAccessor accessor, String name) {
        Object val = accessor.getFirstNativeHeader(name);
        return val != null ? val.toString() : null;
    }
}
