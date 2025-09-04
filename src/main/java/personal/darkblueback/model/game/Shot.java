package personal.darkblueback.model.game;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Shot {

    private String coord;  // "A5"
    private boolean hit;    // true si golpea un submarino

}

