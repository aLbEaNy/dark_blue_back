package personal.darkblueback.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cuentas")
public class Usuario {

    @Id
    private String id;

    private String nickname;
    private String username;
    private String password;
    private boolean activate; //flag de cuenta activada

}