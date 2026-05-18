package lota.project.service;


import lota.project.dto.MessageDtos;

/**
 * Contract for real-time WebSocket message delivery.
 *
 * SRP : isolated from persistence — only deals with "pushing frames to clients".
 * OCP : swap STOMP for raw WS or SockJS without touching callers.
 */
public interface DeliveryService {

    /**
     * Push a message to the recipient's WebSocket session.
     * If the user is offline the message is silently queued for later retrieval.
     */
    void deliver(MessageDtos.MessageResponse message);

    /**
     * Broadcast a system event (join/leave/typing) to all connected clients.
     */
    void broadcastEvent(MessageDtos.SystemEvent event);

    /**
     * Flush any pending (offline-queued) messages to a user who just connected.
     */
    void flushPending(String userId);
}
