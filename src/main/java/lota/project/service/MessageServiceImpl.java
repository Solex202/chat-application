package lota.project.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lota.project.dto.MessageDtos;
import lota.project.model.Message;
import lota.project.model.mapper.MessageMapper;
import lota.project.repository.MessageRepository;
import lota.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Handles message persistence.
 *
 * SRP  : only responsible for CRUD operations on messages.
 * DIP  : depends on MessageRepository and MessageMapper abstractions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDtos.MessageResponse save(MessageDtos.SendRequest request) {
        Message entity = messageMapper.toEntity(request);
        Message saved  = messageRepository.save(entity);
        String senderName = userRepository.findById(request.getSenderId())
                .map(u -> u.getDisplayName())
                .orElse(request.getSenderId());
        log.info("Message #{} saved: {} → {}", saved.getId(),
                request.getSenderId(), request.getReceiverId());
        return messageMapper.toResponse(saved, senderName);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDtos.ConversationResponse getConversation(String userA, String userB) {
        List<MessageDtos.MessageResponse> messages = messageRepository
                .findConversation(userA, userB)
                .stream()
                .map(m -> {
                    String name = userRepository.findById(m.getSenderId())
                            .map(u -> u.getDisplayName())
                            .orElse(m.getSenderId());
                    return messageMapper.toResponse(m, name);
                })
                .toList();
        return MessageDtos.ConversationResponse.builder()
                .participantA(userA)
                .participantB(userB)
                .messages(messages)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDtos.ConversationResponse getRecentMessages(String userA, String userB, int limit) {
        List<MessageDtos.MessageResponse> messages = messageRepository
                .findRecentConversation(userA, userB, limit)
                .stream()
                .sorted((a, b) -> a.getSentAt().compareTo(b.getSentAt())) // re-sort ASC
                .map(m -> {
                    String name = userRepository.findById(m.getSenderId())
                            .map(u -> u.getDisplayName())
                            .orElse(m.getSenderId());
                    return messageMapper.toResponse(m, name);
                })
                .toList();
        return MessageDtos.ConversationResponse.builder()
                .participantA(userA)
                .participantB(userB)
                .messages(messages)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDtos.MessageResponse> getPendingMessages(String userId) {
        return messageRepository.findByReceiverIdAndDeliveredFalse(userId)
                .stream()
                .map(m -> {
                    String name = userRepository.findById(m.getSenderId())
                            .map(u -> u.getDisplayName())
                            .orElse(m.getSenderId());
                    return messageMapper.toResponse(m, name);
                })
                .toList();
    }

    @Override
    public void markDelivered(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));
        message.setDelivered(true);
        messageRepository.save(message);
    }
}
