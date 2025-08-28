package personal.darkblueback.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import personal.darkblueback.model.game.Movimiento;
import personal.darkblueback.model.game.Tablero;

import java.util.List;

@Document(collection = "games")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private String id;

    private int stage; // para el modo campaña del juego
    private String player;   // nickname del jugador humano
    private String maquina = "IA"; // siempre fijo

    private String turnoDe; // "player" o "IA"

    private Boolean isEnd; // false(en curso)

    private Tablero tableroPlayer; // tablero del jugador humano
    private Tablero tableroIA;     // tablero generado aleatoriamente

    private List<Movimiento> movimientos; // historial
    private String ganador; // "player" o "IA"
}

