package personal.darkblueback.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    private List<Submarine> submarines;
    private List<Shot> shots; // ej: {coord:"A5", hit:true, .....}
}
