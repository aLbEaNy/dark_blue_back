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

    public Perfil perfil (Usuario user){
        Perfil perfil = perfilRepository.findByUsername(user.getUsername())
                .orElse(
                        new Perfil(null, user.getUsername(), user.getNickname(), user.getAvatar(),
                                new Stats(
                                        new Date(),
                                        0,0,0,0,0

                                ), null)
                );
        this.savePerfil(perfil);
        return perfil;
    }

    public void savePerfil (Perfil perfil) {
        perfilRepository.save(perfil);
    }
}
