package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.IRestMessage;
import personal.darkblueback.model.game.FireMessage;
import personal.darkblueback.model.game.GamePhase;
import personal.darkblueback.model.gameDTO.GameDTO;
import personal.darkblueback.model.gameDTO.GameMessage;
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
        System.out.println("nickname: " + nickname + " online: " + online + " gameId: " + gameId);
        GameDTO gameDTO;
        //MODO HISTORIA
        if (!online){
            // Crear la partida en servicio
            Game game = gameService.createNewGame(nickname, gameId);

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
    @PostMapping("/shoot")
    public ResponseEntity<GameMessage> shoot(@RequestBody FireMessage fireMsg) {
        GameMessage gameMsg;
        if(fireMsg.getPositions() == null) {
            gameMsg = gameService.processFire(fireMsg);
        } else {
            gameMsg = gameService.processSpecialFire(fireMsg.getGameId(), fireMsg.getMe(), fireMsg.getPositions());
        }

        // notificar a ambos jugadores
        messagingTemplate.convertAndSend(
                "/topic/game/" + fireMsg.getGameId(),
                gameMsg
        );

        return ResponseEntity.ok(gameMsg);
    }

    @PostMapping("/update")
    public ResponseEntity<IRestMessage> updateGame(@RequestBody GameDTO gameDTO) {
        Game game = gameService.mapTOGame(gameDTO);
        //ONLINE
        if(gameDTO.getOnline() && gameDTO.getPhase() == GamePhase.PLACEMENT){
            gameService.sendSocketMessage(gameDTO.getPhase(), gameDTO, null, null);      }
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
    @GetMapping("/getGame/{gameId}")
    public ResponseEntity<IRestMessage> getGame(@PathVariable String gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(()->new RuntimeException("Game not found"));
        GameDTO gameDTO = gameService.mapToDTO(game);
        return ResponseEntity.ok(new IRestMessage(0, "Game con id " + gameId + " recuperado con éxito",gameDTO));
    }
    @GetMapping("/exit/{gameId}")
    public boolean exitGame(@PathVariable String gameId) {
        gameService.sendSocketExit(gameId);
        return true;
    }
}