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
public class Submarine {
    private String id;
    private String tipo; // sub5 , sub4 , sub3a, sub3b , sub2, sub1a, sub1b
    private int sizeSub;
    private List<String> positions; // ej: ["A1","A2","A3"]
    private List<Boolean> isTouched; // true tocado, todos true hundido
    private Boolean isDestroyed; //allTrue(isTouched)
    private Boolean isHorizontal;
}
