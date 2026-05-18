package lota.project.service;



import lota.project.dto.MessageDtos;

import java.util.List;

/**
 * Contract for message persistence and retrieval.
 *
 * SRP : only deals with message data lifecycle.
 * DIP : high-level modules depend on this abstraction, not the impl.
 */
public interface MessageService {

    /** Persist an inbound message and return the enriched response DTO. */
    MessageDtos.MessageResponse save(MessageDtos.SendRequest request);

    /** Return full conversation history between two users. */
    MessageDtos.ConversationResponse getConversation(String userA, String userB);

    /** Return the N most recent messages between two users. */
    MessageDtos.ConversationResponse getRecentMessages(String userA, String userB, int limit);

    /** Return messages sent to a user while they were offline. */
    List<MessageDtos.MessageResponse> getPendingMessages(String userId);

    /** Mark a message as delivered. */
    void markDelivered(Long messageId);
}
