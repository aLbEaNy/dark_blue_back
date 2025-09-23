package personal.darkblueback.model.game;

import lombok.Data;

@Data
public class FireMessage {
    private String gameId;
    private String me; //player1 or player2
    private String pos;
}
