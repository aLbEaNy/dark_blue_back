package personal.darkblueback.model.gameDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Special {
    private String special1;
    private String special2;
    private int counter1;
    private int counter2;
    private boolean activeSpecial1;
    private boolean activeSpecial2;
}
