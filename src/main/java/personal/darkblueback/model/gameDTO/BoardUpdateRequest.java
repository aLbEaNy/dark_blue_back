package personal.darkblueback.model.gameDTO;

import lombok.Data;
import personal.darkblueback.model.game.Board;

@Data
public class BoardUpdateRequest {
    private String player;   // nickname o "IA-BOSS"
    private boolean ready;   // si ya terminó de colocar los submarinos
    private Board board;     // tablero actualizado
}
