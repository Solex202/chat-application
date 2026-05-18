package lota.project.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Persistent chat message entity.
 *
 * SRP: represents only the data contract for a stored message.
 */
@Entity
@Table(name = "messages",
        indexes = {
                @Index(name = "idx_sender",   columnList = "sender_id"),
                @Index(name = "idx_receiver", columnList = "receiver_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(nullable = false, length = 4096)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private Instant sentAt;

    @Column(nullable = false)
    private boolean delivered;

    @PrePersist
    private void prePersist() {
        if (sentAt == null) sentAt = Instant.now();
    }

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING
    }
}
