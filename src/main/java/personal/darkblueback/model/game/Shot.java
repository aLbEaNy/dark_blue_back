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

    private String position;  // "A5"
    private String result;    // si golpea HIT | MISS un submarino

}

