package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Game;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.model.game.Board;
import personal.darkblueback.model.game.Boss;
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
    private final PerfilService perfilService;

    public Game createNewGame(String nickname, Boolean online) {
        Game game = new Game();
        if (!online) {
        //MODO CAMPAÑA O HISTORIA
        gameRepository.deleteByPlayer1(nickname);// Borramos los games del user
        // Datos perfil del player
        Perfil perfil = perfilService.getPerfilByNickname(nickname);

        game.setStage(1);

        game.setPlayer1(nickname);
        game.setAvatarPlayer1(perfil.getAvatar());
        // Datos del boss
        Boss boss = new Boss();
        String nicknameBoss = boss.getNicknameList().get(game.getStage());
        boss.setAvatarBoss("http://localhost:8080/media/images/avatar/boss"+game.getStage()+".png");
        game.setPlayer2(nicknameBoss);
        game.setAvatarPlayer2(boss.getAvatarBoss());

        // // Estado inicial de la partida
        game.setPhase(GamePhase.PLACEMENT);
        game.setTurn(random.nextBoolean() ? nickname : nicknameBoss);
        game.setIsEnd(false);
        game.setReadyPlayer1(false);
        game.setReadyPlayer2(true);

        // Crear tableros con submarinos aleatorios
        game.setBoardPlayer1(generateBoard());
        game.setBoardPlayer2(generateBoard());
        } else {
            //TODO MODO ONLINE

        }

        return game;
    }

    private Board generateBoard() {
        Board board = new Board();

        List<Submarine> submarines = new ArrayList<>();
        Set<String> occupied = new HashSet<>(); // celdas ya ocupadas

        // FLOTA DE SUBMARINOS
        submarines.add(generateSubmarine("s5","sub5", 5, occupied));
        submarines.add(generateSubmarine("s4","sub4", 4, occupied));
        submarines.add(generateSubmarine("s3","sub3a", 3, occupied));
        submarines.add(generateSubmarine("s3-2","sub3b", 3, occupied));
        submarines.add(generateSubmarine("s2","sub2", 2, occupied));
        submarines.add(generateSubmarine("s1","sub1a", 1, occupied));
        submarines.add(generateSubmarine("s1-2","sub1b", 1, occupied));

        board.setSubmarines(submarines);
        board.setShots(new ArrayList<>());

        return board;
    }

    private Submarine generateSubmarine(String id, String tipo, int size, Set<String> occupied) {
        Submarine sub = new Submarine();
        sub.setId(id);
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
            game.setPhase(GamePhase.BATTLE);
        }

        return gameRepository.save(game);
    }


    public GameDTO mapToDTO(Game game, String currentNickname) {

        GameDTO dto = new GameDTO();
        //  Para saber quien es quien importante!
        if (currentNickname.equals(game.getPlayer1())) {
            dto.setMe("player1");
        } else if (currentNickname.equals(game.getPlayer2())) {
                dto.setMe("player2");
        }
        dto.setGameId(game.getId());
        dto.setStage(game.getStage());
        dto.setPhase(game.getPhase());
        dto.setPlayer1(game.getPlayer1());
        dto.setAvatarPlayer1(game.getAvatarPlayer1());
        dto.setPlayer2(game.getPlayer2());
        dto.setAvatarPlayer2(game.getAvatarPlayer2());
        dto.setTurn(game.getTurn());
        dto.setIsEnd(game.getIsEnd());
        dto.setWinner(game.getWinner());
        dto.setBoardPlayer1(game.getBoardPlayer1());
        dto.setBoardPlayer2(game.getBoardPlayer2());
        dto.setReadyPlayer1(game.isReadyPlayer1());
        dto.setReadyPlayer2(game.isReadyPlayer2());
        return dto;
    }

}
