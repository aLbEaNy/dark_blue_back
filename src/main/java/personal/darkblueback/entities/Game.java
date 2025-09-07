package personal.darkblueback.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import personal.darkblueback.model.game.Board;
import personal.darkblueback.model.game.GamePhase;
import personal.darkblueback.model.game.Movement;

import java.util.List;

@Document(collection = "games")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private String id; //gameId lo genera mongoDB

    private int stage; // para el modo campaña del juego
    private GamePhase phase = GamePhase.PLACEMENT;
    private String player1;   // nickname del jugador humano
    private String avatarPlayer1;
    private String player2; // será en modo campaña el boos de la IA
    private String avatarPlayer2; //el del boss en modo historia

    private String me; // "player1" o "player2" para saber en online quien soy y que tablero es el mio

    private String turn; // "${nickname}" o "${boss}"

    private Boolean isEnd; // false(en curso)

    private Board boardPlayer1;
    private Board boardPlayer2;

    private boolean readyPlayer1;
    private boolean readyPlayer2;


    private List<Movement> moves; // historial
    private String winner; // "${nickname}" o "${boss}"
}

