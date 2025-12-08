package personal.darkblueback.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiFireRequest {
    private Board board;   // el tablero del rival, con submarinos y disparos
    private String special;   // "multiShot" | "laserShot" | null
    private String gameId;
    private String nickname;
}
