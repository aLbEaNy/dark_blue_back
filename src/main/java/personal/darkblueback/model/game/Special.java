package personal.darkblueback.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Special {
    private String name;
    private String description;
    private String image;
    private Boolean active;
    private String owner;
}
