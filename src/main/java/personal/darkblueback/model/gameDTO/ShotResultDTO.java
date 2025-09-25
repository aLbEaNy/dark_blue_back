package personal.darkblueback.model.gameDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShotResultDTO {
    private boolean hit;
    private boolean miss;
    private boolean destroyed;
}
