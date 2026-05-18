package lota.project.model.mapper;

import lota.project.dto.MessageDtos;
import lota.project.model.Message;
import lota.project.model.enums.MessageType;
import org.springframework.stereotype.Component;

/**
 * Pure transformation — no I/O, easily unit-tested without Spring context.
 */
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public Message toEntity(MessageDtos.SendRequest request) {
        return Message.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : MessageType.CHAT)
                .delivered(false)
                .build();
    }

    @Override
    public MessageDtos.MessageResponse toResponse(Message message, String senderDisplayName) {
        return MessageDtos.MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(senderDisplayName)
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .type(message.getType())
                .sentAt(message.getSentAt())
                .delivered(message.isDelivered())
                .build();
    }
}
