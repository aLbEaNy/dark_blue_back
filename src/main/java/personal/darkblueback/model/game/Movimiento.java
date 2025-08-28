package personal.darkblueback.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movimiento {
    private String jugador;     // quién dispara
    private String coordenada;  // "A5"
    private boolean acierto;    // true si golpea un barco
    private boolean hundido;    // true si hundió un barco
}

