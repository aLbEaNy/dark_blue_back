package personal.darkblueback.model.game;

import lombok.Data;

@Data
public class FireMessage {
    private String gameId;
    private String player;
    private String coord;
}
