package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.IRestMessage;
import personal.darkblueback.model.gameDTO.BoardUpdateRequest;
import personal.darkblueback.model.gameDTO.GameDTO;
import personal.darkblueback.repository.GameRepository;
import personal.darkblueback.services.GameService;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameRepository gameRepository;

    @GetMapping("/new")
    public ResponseEntity<IRestMessage> newGame(@RequestParam String nickname, @RequestParam Boolean online) {
        //TODO implementar ONLINE
        // 1. Crear la partida en servicio
        Game newGame = gameService.createNewGame(nickname);

        // 2. Persistir en Mongo
        gameRepository.save(newGame);
        // 3. Mapear a DTO
        GameDTO gameDTO = gameService.mapToDTO(newGame);
        return ResponseEntity.ok(new IRestMessage(0, "Partida creada", gameDTO));
    }
    @PutMapping("/{gameId}/board")
    public ResponseEntity<IRestMessage> updateBoard(
            @PathVariable String gameId,
            @RequestBody BoardUpdateRequest request
    ) {
        Game updatedGame = gameService.updateBoard(gameId, request);
        GameDTO gameDTO = gameService.mapToDTO(updatedGame);
        return ResponseEntity.ok(new IRestMessage(0, "Partida actualizada", gameDTO));
    }

}