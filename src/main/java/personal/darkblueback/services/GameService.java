package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Game;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.model.game.*;
import personal.darkblueback.model.gameDTO.GameDTO;
import personal.darkblueback.model.gameDTO.GameMessage;
import personal.darkblueback.model.gameDTO.ShotResultDTO;
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
    private final SimpMessagingTemplate messagingTemplate;


    public Game createNewGame(String nickname, Boolean online, Game game) {
        Perfil perfil = perfilService.getPerfilByNickname(nickname);
        if(!online){
        //MODO HISTORIA
            // Continua la partida
            if (game.getId() != null) {
                System.out.println("Continuando partida");
                game.setWinner("player1");
                int _stage = game.getStage();
                game.setStage(_stage + 1);
                System.out.println("-----------> GAME STAGE " + game.getStage());
            } else {
                System.out.println("Nueva partida");
            //Nueva partida
                gameRepository.deleteByPlayer1(nickname);// Borramos los games del user
                game.setStage(1);
                game.setOnline(online);
                game.setPlayer1(nickname);
                game.setAvatarPlayer1(perfil.getAvatar());
            }

            // Estado inicial de la partida
            game.setPhase(GamePhase.PLACEMENT);
            game.setTurn(random.nextBoolean() ? "player1" : "player2");
            game.setIsEnd(false);
            // Crear tableros con submarinos aleatorios
            game.setBoardPlayer1(generateBoard());
            game.setBoardPlayer2(generateBoard());
            // Datos del boss
            Boss boss = new Boss();
            String nicknameBoss = boss.getNicknameList().get(game.getStage());

            game.setReadyPlayer1(false);
            game.setReadyPlayer2(true);

            boss.setAvatarBoss("http://192.168.1.136:8080/media/images/avatar/boss"+game.getStage()+".png");
                System.out.println(nicknameBoss);
                System.out.println(boss.getAvatarBoss());
            game.setPlayer2(nicknameBoss);
            game.setAvatarPlayer2(boss.getAvatarBoss());

        }else{
            //TODO MODO ONLINE


            game.setTurn(random.nextBoolean() ? "player1" : "player2");


        }
        //Persistir en Mongo
        gameRepository.save(game);
        return game;
    }

    public GameDTO processShot(String gameId, String player, String coord) {
        Game game = gameRepository.findById(gameId).orElseThrow(()->new CustomException("Game not found"));

        GameDTO gameDTO = mapToDTO(game);
        return  gameDTO;
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
        submarines.add(generateSubmarine("s3-3","sub3b", 3, occupied));
        submarines.add(generateSubmarine("s2","sub2", 2, occupied));
        submarines.add(generateSubmarine("s2-2","sub2", 2, occupied));
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

    public GameDTO mapToDTO(Game game) {

        GameDTO dto = new GameDTO();

        dto.setGameId(game.getId());
        dto.setOnline(game.getOnline());
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

    public Game mapTOGame(GameDTO dto) {
        Game game = new Game();
        game.setId(String.valueOf(dto.getGameId()));
        game.setOnline(dto.getOnline());
        game.setStage(dto.getStage());
        game.setPhase(dto.getPhase());
        game.setPlayer1(dto.getPlayer1());
        game.setAvatarPlayer1(dto.getAvatarPlayer1());
        game.setPlayer2(dto.getPlayer2());
        game.setAvatarPlayer2(dto.getAvatarPlayer2());
        game.setTurn(dto.getTurn());
        game.setIsEnd(dto.getIsEnd());
        game.setBoardPlayer1(dto.getBoardPlayer1());
        game.setBoardPlayer2(dto.getBoardPlayer2());
        game.setReadyPlayer1(dto.getReadyPlayer1());
        game.setReadyPlayer2(dto.getReadyPlayer2());
        game.setWinner(dto.getWinner());
        return game;
    }

    //ONLINE
    public GameDTO joinOrCreateGame(String nickname) {
        Optional<Game> optionalGame = gameRepository.findFirstByOnlineTrueAndPlayer1NotNullAndPlayer2IsNull();

        Game game = new Game();
        Perfil perfil = perfilService.getPerfilByNickname(nickname);

        boolean isNew = false;

        if (optionalGame.isPresent()) {
            game = optionalGame.get();
            if(game.getPlayer1().equals(nickname)) return mapToDTO(game);
            // Asignar jugador al slot libre player2
            game.setPlayer2(nickname);
            game.setAvatarPlayer2(perfil.getAvatar());

        } else {
            // No hay juego disponible, crear uno nuevo

            game.setOnline(true);
            game.setPlayer1(nickname);
            game.setAvatarPlayer1(perfil.getAvatar());

            game.setTurn(random.nextBoolean() ? "player1" : "player2");
            game.setReadyPlayer1(false);
            game.setReadyPlayer2(false);

            game.setBoardPlayer1(generateBoard());
            game.setBoardPlayer2(generateBoard());
            game.setIsEnd(false);

            isNew = true;
        }

        // Notificar por WebSocket si la partida ya tiene ambos jugadores
        if (!isNew && game.getPlayer2() !=null) {
            game.setPhase(GamePhase.JOINED);
            sendSocketMessage( game.getPhase(), mapToDTO(game), null);
            System.out.println("Notificando jugadores conectados");
        }
        // Guardar en Mongo
        gameRepository.save(game);
        GameDTO gameDTO = mapToDTO(game);

        return gameDTO;
    }
    public GameMessage processFire(FireMessage fireMsg) {
        Game game = gameRepository.findById(fireMsg.getGameId()).orElseThrow(()->new CustomException("Game not found"));

        // 1. Validar que es turno correcto
        if (!game.getTurn().equals(fireMsg.getMe())) {
            throw new IllegalStateException("Not your turn!");
        }

        // 2. Procesar disparo sobre tablero rival
        Board boardRival = fireMsg.getMe().equals("player1")
                ? game.getBoardPlayer2()
                : game.getBoardPlayer1();

        String pos = fireMsg.getPos();
        boolean hit = false;

        // 3. Buscar si acierta algún submarino
        ShotResultDTO lastShot = new ShotResultDTO(false, false, false);
        for (Submarine sub : boardRival.getSubmarines()) {
            int idx = sub.getPositions().indexOf(pos);
            if (idx != -1) {
                hit = true;
                sub.getIsTouched().set(idx, true);
                lastShot.setHit(true);

                // comprobar si ese submarino queda destruido
                if (sub.getIsTouched().stream().allMatch(Boolean::booleanValue)) {
                    sub.setIsDestroyed(true);
                    lastShot.setDestroyed(true);
                }
                break;
            }
        }
        if(!lastShot.isHit())
            lastShot.setMiss(true);

        // 4. Registrar disparo en el tablero rival
        boardRival.getShots().add(new Shot(pos, hit ? "HIT" : "MISS"));

        // 5. ¿Se acabó la partida?
        boolean allDestroyed = boardRival.getSubmarines().stream()
                .allMatch(Submarine::getIsDestroyed);

        if (allDestroyed) {
            game.setWinner(fireMsg.getMe());
            game.setPhase(GamePhase.END);
            game.setIsEnd(true);
        } else {
            // 6. Actualizar turno (solo si fallo)
            if (!hit) {
                game.setTurn(fireMsg.getMe().equals("player1") ? "player2" : "player1");
            }
        }
        // 7. Persistir cambios
        gameRepository.save(game);

        // 8. Construir respuesta
        GameMessage msg = new GameMessage();
        msg.setPhase(game.getPhase());
        msg.setGame(mapToDTO(game));
        msg.setLastShot(lastShot);

        return msg;
    }

    public void sendSocketMessage (GamePhase phase, GameDTO game, ShotResultDTO lastShot) {
        // Enviar a todos los clientes en el topic de ese gameId
        messagingTemplate.convertAndSend(
                "/topic/game/" + game.getGameId(),
                new GameMessage(phase, game, lastShot)
        );
    }
}


