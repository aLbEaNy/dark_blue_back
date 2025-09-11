package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.IRestMessage;
import personal.darkblueback.model.game.FireMessage;
import personal.darkblueback.model.gameDTO.GameDTO;
import personal.darkblueback.repository.GameRepository;
import personal.darkblueback.services.GameService;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;
    private final GameRepository gameRepository;

    @GetMapping("/new")
    public ResponseEntity<IRestMessage> newGame(@RequestParam String nickname, @RequestParam Boolean online) {
        System.out.println("nickname: " + nickname + " online: " + online);
        GameDTO gameDTO = new GameDTO();
        if (Boolean.FALSE.equals(online)) {
        // ----------- MODO HISTORIA ---------
        // 1. Crear la partida en servicio
        Game newGame = gameService.createNewGame(nickname, false);

        // 2. Persistir en Mongo
        gameRepository.save(newGame);
        // 3. Mapear a DTO
        gameDTO = gameService.mapToDTO(newGame);

        } else {
            //TODO implementar ONLINE
        }
        return ResponseEntity.ok(new IRestMessage(0, "Partida creada", gameDTO));
    }
    // Cliente envía un disparo
    @MessageMapping("/fire")
    public void fire(FireMessage message) {
        GameDTO updatedGame = gameService.processShot(message.getGameId(), message.getPlayer(), message.getCoord());

        // Envías el estado actualizado a todos los clientes del juego
        messagingTemplate.convertAndSend("/topic/game/" + message.getGameId(), updatedGame);
    }

}