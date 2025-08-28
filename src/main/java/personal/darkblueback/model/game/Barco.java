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
public class Barco {
    private String tipo; // acorazado, destructor, submarino
    private int tamaño;
    private List<String> posiciones; // ej: ["A1","A2","A3"]
    private boolean hundido;
}
