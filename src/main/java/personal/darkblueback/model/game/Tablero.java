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
public class Tablero {
    private List<Barco> barcos;
    private List<String> disparosRecibidos; // ej: ["A1","B5"]
}
