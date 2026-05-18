package lota.project.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Chat user — stored in H2 on registration.
 *
 * SRP: only holds user identity data.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {

    @Id
    private String id;         // username chosen by the user

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private Instant joinedAt;

    @Column(nullable = false)
    private boolean online;

    @PrePersist
    private void prePersist() {
        if (joinedAt == null) joinedAt = Instant.now();
    }
}
