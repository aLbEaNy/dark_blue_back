package personal.darkblueback.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import personal.darkblueback.model.Stats;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "perfiles")
public class Perfil {

    @Id
    private String id;

    private String username;

    private String nickname;

    private String avatar;

    private Stats stats;

    private String idActualGame = null;





}
