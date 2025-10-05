package personal.darkblueback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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

}
