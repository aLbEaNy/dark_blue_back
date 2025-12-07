package personal.darkblueback.model.gameDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.darkblueback.model.game.GamePhase;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameMessage {
    private GamePhase phase;   // "JOINED", "READY", "BATTLE", "END"
    private GameDTO game;  // partida completa
    private ShotResultDTO lastShot; // null si no hay disparo
    private List<ShotResultDTO> multiShotResults;

    private String player;
    private String special;
    private Integer slot;

    private String type;
    private String sender;
    private String content;
    private String timestamp;
}
