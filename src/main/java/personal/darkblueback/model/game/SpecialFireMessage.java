package personal.darkblueback.model.game;

import lombok.Data;

import java.util.List;

@Data
public class SpecialFireMessage {
    private String gameId;
    private String me; // player1 o player2
    private List<String> positions; // posiciones a disparar
    private String specialType; // "multiShot" o "laserShot"
}
