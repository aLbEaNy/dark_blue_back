package personal.darkblueback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Este es tu endpoint STOMP para Angular
        registry.addEndpoint("/ws-game")
                .setAllowedOrigins("http://localhost:4200","http://192.168.1.136:4200"); // permite Angular
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configuración del broker de mensajes
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
