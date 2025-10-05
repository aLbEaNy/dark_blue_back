package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.IRestMessage;
import personal.darkblueback.model.game.FireMessage;
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
    public ResponseEntity<IRestMessage> newGame(@RequestParam String nickname, @RequestParam Boolean online) {
        System.out.println("nickname: " + nickname + " online: " + online);
        Game game = new Game();
        GameDTO gameDTO;
        //MODO HISTORIA
        if (!online){
            // Crear la partida en servicio
            game = gameService.createNewGame(nickname, online, game);

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

        GameMessage gameMsg = gameService.processFire(fireMsg);

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
        if(gameDTO.getOnline()){
            switch (gameDTO.getPhase()){
                case PLACEMENT -> {
                    System.out.println("(UPDATE) PHASE PLACEMENT ONLINE");
                    System.out.println("GAME READY " +gameDTO.getReadyPlayer1() + " " + gameDTO.getReadyPlayer2());
                    if(gameDTO.getReadyPlayer1()){
                        System.out.println("ENVIO SOCKET EN PHASE PLACEMENT READY player1");
                    }
                    if(gameDTO.getReadyPlayer2()){
                        System.out.println("ENVIO SOCKET EN PHASE PLACEMENT READY player2");
                    }
                }
                case BATTLE -> {
                    System.out.println("UPDATE PHASE BATTLE");
                }

            }
            gameService.sendSocketMessage(gameDTO.getPhase(), gameDTO, null);

        }

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

}