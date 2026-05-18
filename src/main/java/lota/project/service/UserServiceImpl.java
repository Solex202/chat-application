package lota.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lota.project.dto.MessageDtos;
import lota.project.exception.UserNotFoundException;
import lota.project.model.ChatUser;
import lota.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public MessageDtos.UserResponse registerOrGet(MessageDtos.UserRequest request) {
        ChatUser user = userRepository.findById(request.getUserId())
                .orElseGet(() -> {
                    log.info("Registering new user: {}", request.getUserId());
                    return userRepository.save(ChatUser.builder()
                            .id(request.getUserId())
                            .displayName(request.getDisplayName())
                            .online(true)
                            .build());
                });
        user.setOnline(true);
        user.setDisplayName(request.getDisplayName());
        return toResponse(userRepository.save(user));
    }

    @Override
    public MessageDtos.UserResponse setOnline(String userId, boolean online) {
        ChatUser user = findOrThrow(userId);
        user.setOnline(online);
        log.info("User {} is now {}", userId, online ? "online" : "offline");
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDtos.UserResponse> getOnlineUsers() {
        return userRepository.findByOnlineTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDtos.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDtos.UserResponse getUser(String userId) {
        return toResponse(findOrThrow(userId));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private ChatUser findOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private MessageDtos.UserResponse toResponse(ChatUser u) {
        return MessageDtos.UserResponse.builder()
                .id(u.getId())
                .displayName(u.getDisplayName())
                .online(u.isOnline())
                .joinedAt(u.getJoinedAt())
                .build();
    }
}
