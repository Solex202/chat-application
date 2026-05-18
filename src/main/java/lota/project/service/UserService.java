package lota.project.service;


import lota.project.dto.MessageDtos;

import java.util.List;

/**
 * Contract for user registration, presence, and lookup.
 *
 * SRP : only deals with user lifecycle — separate from message logic.
 */
public interface UserService {

    MessageDtos.UserResponse registerOrGet(MessageDtos.UserRequest request);

    MessageDtos.UserResponse setOnline(String userId, boolean online);

    List<MessageDtos.UserResponse> getOnlineUsers();

    List<MessageDtos.UserResponse> getAllUsers();

    MessageDtos.UserResponse getUser(String userId);
}
