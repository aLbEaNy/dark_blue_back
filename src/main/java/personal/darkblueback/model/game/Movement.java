package personal.darkblueback.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movement {
    private String turn;     // quién dispara
    private String coord;  // "A5"
    private boolean touched;    // true si golpea un barco
    private boolean destroy;    // true si hundió un barco
}

