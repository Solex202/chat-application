package lota.project.repository;

import lota.project.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for messages.
 *
 * OCP: extend queries by adding methods — never modify existing ones.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Fetch the full conversation between two users in chronological order.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :a AND m.receiverId = :b)
           OR (m.senderId = :b AND m.receiverId = :a)
        ORDER BY m.sentAt ASC
        """)
    List<Message> findConversation(@Param("a") String userA,
                                   @Param("b") String userB);

    /**
     * Most recent N messages in a conversation (for history load).
     */
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :a AND m.receiverId = :b)
           OR (m.senderId = :b AND m.receiverId = :a)
        ORDER BY m.sentAt DESC
        LIMIT :limit
        """)
    List<Message> findRecentConversation(@Param("a") String userA,
                                         @Param("b") String userB,
                                         @Param("limit") int limit);

    /**
     * Undelivered messages for a user (sent while offline).
     */
    List<Message> findByReceiverIdAndDeliveredFalse(String receiverId);
}
