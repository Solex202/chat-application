package lota.project.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * Configures the STOMP WebSocket broker.
 *
 * Clients connect to ws://localhost:8080/ws
 * then SUBSCRIBE to:
 *   /user/{userId}/queue/messages  — personal inbox
 *   /topic/events                  — broadcast (joins/leaves)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();   // SockJS fallback for browsers without native WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for @MessageMapping methods in controllers
        registry.setApplicationDestinationPrefixes("/app");

        // In-memory broker for topics and user queues
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix used by convertAndSendToUser()
        registry.setUserDestinationPrefix("/user");
    }
}
