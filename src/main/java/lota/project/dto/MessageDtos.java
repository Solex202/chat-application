package lota.project.dto;




import lombok.*;
import lota.project.model.Message;

import java.time.Instant;

/**
 * DTO layer — decouples the wire format from the domain model.
 *
 * ISP: each record carries only the fields its consumer needs.
 */
public final class MessageDtos {

    private MessageDtos() {}

    /** Inbound: client → server */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendRequest {
        private String senderId;
        private String receiverId;
        private String content;
        private Message.MessageType type;
    }

    /** Outbound: server → client */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageResponse {
        private Long   id;
        private String senderId;
        private String senderName;
        private String receiverId;
        private String content;
        private Message.MessageType type;
        private Instant sentAt;
        private boolean delivered;
    }

    /** System event broadcast (user joined / left) */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemEvent {
        private String eventType;   // "USER_JOINED" | "USER_LEFT" | "TYPING"
        private String userId;
        private String displayName;
        private Instant timestamp;
    }

    /** REST response for message history */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConversationResponse {
        private String  participantA;
        private String  participantB;
        private java.util.List<MessageResponse> messages;
    }

    /** User registration/login */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRequest {
        private String userId;
        private String displayName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponse {
        private String  id;
        private String  displayName;
        private boolean online;
        private Instant joinedAt;
    }
}
