package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.game.Board;
import personal.darkblueback.model.game.GamePhase;
import personal.darkblueback.model.game.Submarine;
import personal.darkblueback.model.gameDTO.BoardUpdateRequest;
import personal.darkblueback.model.gameDTO.GameDTO;
import personal.darkblueback.repository.GameRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final Random random = new Random();
    private static final int BOARD_SIZE = 10;  // 10 filas x 10 columnas
    private static final char[] ROWS = "ABCDEFGHIJ".toCharArray();
    private final GameRepository gameRepository;

    public Game createNewGame(String nickname) {

        // Borrar todos los juegos previos del usuario
        gameRepository.deleteByPlayer1(nickname);

        Game game = new Game();
        game.setStage(1);
        game.setPlayer1(nickname);
        game.setPlayer2("IA-BOSS");
        // El turno es aleatorio entre nickname y "IA-BOSS"
        game.setTurn(random.nextBoolean() ? nickname : "IA-BOSS");
        game.setIsEnd(false);
        game.setReadyPlayer1(false);
        game.setReadyPlayer2(true);

        // Crear tableros con submarinos aleatorios
        game.setBoardPlayer1(generateBoard());
        game.setBoardPlayer2(generateBoard());

        return game;
    }

    private Board generateBoard() {
        Board board = new Board();

        List<Submarine> submarines = new ArrayList<>();
        Set<String> occupied = new HashSet<>(); // celdas ya ocupadas

        // FLOTA DE SUBMARINOS
        submarines.add(generateSubmarine("sub5", 5, occupied));
        submarines.add(generateSubmarine("sub4", 4, occupied));
        submarines.add(generateSubmarine("sub3a", 3, occupied));
        submarines.add(generateSubmarine("sub3b", 3, occupied));
        submarines.add(generateSubmarine("sub2", 2, occupied));
        submarines.add(generateSubmarine("sub1a", 1, occupied));
        submarines.add(generateSubmarine("sub1b", 1, occupied));

        board.setSubmarines(submarines);
        board.setShots(new ArrayList<>());

        return board;
    }

    private Submarine generateSubmarine(String tipo, int size, Set<String> occupied) {
        Submarine sub = new Submarine();
        sub.setTipo(tipo);
        sub.setSizeSub(size);

        boolean horizontal;
        List<String> posiciones;

        // Intentar hasta encontrar una posición válida
        while (true) {
            posiciones = new ArrayList<>();
            horizontal = random.nextBoolean();

            int rowIndex = random.nextInt(BOARD_SIZE);
            int colIndex = random.nextInt(BOARD_SIZE);

            if (horizontal) {
                // comprobar si cabe en horizontal
                if (colIndex + size > BOARD_SIZE) continue;

                for (int i = 0; i < size; i++) {
                    String coord = ROWS[rowIndex] + String.valueOf(colIndex + 1 + i);
                    posiciones.add(coord);
                }
            } else {
                // comprobar si cabe en vertical
                if (rowIndex + size > BOARD_SIZE) continue;

                for (int i = 0; i < size; i++) {
                    String coord = ROWS[rowIndex + i] + String.valueOf(colIndex + 1);
                    posiciones.add(coord);
                }
            }

            // comprobar colisiones
            boolean collision = posiciones.stream().anyMatch(occupied::contains);
            if (!collision) break; // válido → salimos del bucle
        }

        // marcar ocupadas
        occupied.addAll(posiciones);

        sub.setPositions(posiciones);

        // impactos
        List<Boolean> isTouched = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            isTouched.add(false);
        }
        sub.setIsTouched(isTouched);
        sub.setIsDestroyed(false);
        sub.setIsHorizontal(horizontal);

        return sub;
    }

    public Game updateBoard(String gameId, BoardUpdateRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Determinar qué jugador actualiza
        if (request.getPlayer().equals(game.getPlayer1())) {
            game.setBoardPlayer1(request.getBoard());
            game.setReadyPlayer1(request.isReady());
        } else if (request.getPlayer().equals(game.getPlayer2())) {
            game.setBoardPlayer2(request.getBoard());
            game.setReadyPlayer2(request.isReady());
        } else {
            throw new RuntimeException("Player not part of this game");
        }

        // 🚀 Si ambos jugadores están listos, avanzar fase
        if (game.isReadyPlayer1() && game.isReadyPlayer2()) {
            game.setPhase(GamePhase.IN_PROGRESS);
        }

        return gameRepository.save(game);
    }


    public GameDTO mapToDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getStage(),
                game.getPhase(),      // fase de la partida
                game.getPlayer1(),
                game.getPlayer2(),
                game.getTurn(),
                game.getIsEnd(),
                game.getWinner(),
                game.getBoardPlayer1(),
                game.getBoardPlayer2(),
                game.isReadyPlayer1(),
                game.isReadyPlayer2()
        );
    }

}
