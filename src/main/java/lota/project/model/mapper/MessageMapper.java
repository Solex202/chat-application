package lota.project.model.mapper;

import lota.project.dto.MessageDtos;
import lota.project.model.Message;

public interface MessageMapper {

    Message toEntity(MessageDtos.SendRequest request);
    MessageDtos.MessageResponse toResponse(Message message, String senderDisplayName);
}
