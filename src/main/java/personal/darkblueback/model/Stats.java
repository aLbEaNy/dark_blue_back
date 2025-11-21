package personal.darkblueback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stats {

    private Date fechaRegistro;
    private int stage = 0;
    private int coins = 0;
    private int wins = 0;
    private int losses = 0;
    private Long playTime = 0L; // total acumulado en milisegundos
    private Long currentStartTime; // timestamp cuando comienza una sesión
    private String rango = "Marinero";
    private List<String> specials;
    private String specialSlot1;
    private String specialSlot2;
}
