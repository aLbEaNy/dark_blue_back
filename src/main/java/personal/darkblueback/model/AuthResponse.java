package personal.darkblueback.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import personal.darkblueback.entities.Perfil;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Perfil perfil;
}
