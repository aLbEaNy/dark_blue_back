package personal.darkblueback.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import personal.darkblueback.model.gameDTO.GameMessage;
import java.time.LocalDateTime;

@Controller
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public SocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{gameId}") // cliente envía a /app/chat/{gameId}
    public void handleChat(@DestinationVariable String gameId, GameMessage msg) {
        msg.setType("CHAT");
        msg.setTimestamp(LocalDateTime.now().toString());
        System.out.println("📩 [CHAT][" + gameId + "] " + msg.getSender() + ": " + msg.getContent());

        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId,  // reenviamos solo a los jugadores de esa partida
                msg
        );
    }
    @MessageMapping("/game/{gameId}")
    public void handleGame(@DestinationVariable String gameId, GameMessage msg) {
        msg.setType("GAME");
        msg.setTimestamp(LocalDateTime.now().toString());

        // Reenvío a todos los suscriptores
        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId,
                msg
        );
    }
}
