package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<IRestMessage> newGame(@RequestParam String nickname, @RequestParam Boolean online, @RequestParam String gameId) {
        System.out.println("nickname: " + nickname + " online: " + online + " gameContinue: " + gameId);
        Game game = new Game();
        GameDTO gameDTO;
        //MODO HISTORIA
        if (!online){

            if (!gameId.isEmpty()) {
                Game gameContinue = gameRepository.findById(gameId).orElse(null);
                game = gameService.createNewGame(nickname, online, gameContinue);
            } else{
                // Crear la partida en servicio
                game = gameService.createNewGame(nickname, online, game);

            }
            // 2. Persistir en Mongo
            gameRepository.save(game);
            // 3. Mapear a DTO
            gameDTO = gameService.mapToDTO(game);

            return ResponseEntity.ok(new IRestMessage(0, "Partida creada", gameDTO));

        //MODO ONLINE PvP
        }else{
            gameDTO = gameService.joinOrCreateGame(nickname);

            return ResponseEntity.ok(new IRestMessage(0, "Partida online creada", gameDTO));
        }
    }
    // Cliente envía un disparo
    @MessageMapping("/fire")
    public void fire(FireMessage message) {
        GameDTO updatedGame = gameService.processShot(message.getGameId(), message.getPlayer(), message.getCoord());

        // Envías el estado actualizado a todos los clientes del juego
        messagingTemplate.convertAndSend("/topic/game/" + message.getGameId(), updatedGame);
    }

    @PostMapping("/update")
    public ResponseEntity<IRestMessage> updateGame(@RequestBody GameDTO gameDTO) {
        Game game = gameService.mapTOGame(gameDTO);
        gameRepository.save(game);
        return ResponseEntity.ok(new IRestMessage(0, "Partida actualizada", gameDTO));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<IRestMessage> deleteGame(@PathVariable String gameId) {
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity
                    .status(404)
                    .body(new IRestMessage(1, "Partida no encontrada", null));
        }

        gameRepository.deleteById(gameId);
        return ResponseEntity.ok(new IRestMessage(0, "Partida eliminada", null));
    }


}