package personal.darkblueback.model.game;

import lombok.Data;

import java.util.List;

@Data
public class FireMessage {
    private String gameId;
    private String me; //player1 or player2
    private String pos;
    private List<String> positions;
}
