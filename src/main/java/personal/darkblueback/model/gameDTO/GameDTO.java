package personal.darkblueback.model.gameDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.darkblueback.model.game.Board;
import personal.darkblueback.model.game.GamePhase;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private String gameId;
    private Boolean online;
    private int stage;
    private GamePhase phase;

    private String player1;
    private String avatarPlayer1;
    private String player2;
    private String avatarPlayer2;

    private String turn;

    private Boolean isEnd;
    private String winner;

    private Board boardPlayer1;
    private Board boardPlayer2;

    private Boolean readyPlayer1;
    private Boolean readyPlayer2;

    private Special specialPlayer1;
    private Special specialPlayer2;
}
