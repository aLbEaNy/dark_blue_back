package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.model.Stats;
import personal.darkblueback.repository.PerfilRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public Perfil getPerfilByNickname (String nickname) {
        return perfilRepository.findByNickname(nickname).orElse(null);
    }
    public Perfil getPerfilByUsername (String username) {
        return perfilRepository.findByUsername(username).orElse(null);
    }

    public Perfil newPerfil (Usuario user, String avatar){
        Perfil perfil = perfilRepository.findByUsername(user.getUsername())
                .orElse(
                        new Perfil(null, user.getUsername(), user.getNickname(), avatar,
                                new Stats(
                                        new Date(),
                                        0,0,0,0

                                ), null)
                );
        this.savePerfil(perfil);
        return perfil;
    }

    public void savePerfil (Perfil perfil) {
        perfilRepository.save(perfil);
    }
    public void changeAvatar (Usuario user, String avatar) {
        Perfil perfil = perfilRepository.findByUsername(user.getUsername())
                .orElse( newPerfil(user, avatar)); // Si no tuviera perfil por no haber sido verificada la cuenta
        perfil.setAvatar(avatar);
        this.savePerfil(perfil);
    }
}
