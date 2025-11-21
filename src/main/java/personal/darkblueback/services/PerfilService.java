package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.model.Stats;
import personal.darkblueback.repository.PerfilRepository;
import personal.darkblueback.repository.UsuarioRepository;
import personal.darkblueback.utils.PasswordUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${storage.media.url}")
    private String storageUrl;
    @Value("${storage.media.avatar-dir}")
    private  String AVATAR_DIR;

    public Perfil getPerfilByNickname (String nickname) {
        return perfilRepository.findByNickname(nickname).orElse(null);
    }
    public Perfil getPerfilByUsername (String username) {
        return perfilRepository.findByUsername(username).orElse(null);
    }

    public Perfil newPerfil (Usuario user, String avatar){
        Perfil perfil = perfilRepository.findByUsername(user.getUsername())
                .orElse(
                        new Perfil(null, user.getUsername(), user.getNickname(), avatar+"?t="+System.currentTimeMillis(),
                                new Stats(
                                        new Date(),
                                        0,0,0,0,0L,0L,"Marinero", new ArrayList<String>(),"",""

                                ), null)
                );
        this.savePerfil(perfil);
        return perfil;
    }
    public Perfil updateProfile(String username, String nickname, String password, MultipartFile avatarFile) throws IOException {
        Perfil perfil = perfilRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario user = usuarioRepository.findByUsername(username).orElseThrow(()->new CustomException("Usuario no encontrado"));

        if (nickname != null && !nickname.isEmpty()) {
            perfil.setNickname(nickname);
            user.setNickname(nickname);
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(password));
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = perfil.getUsername().split("@")[0] + ".png";
            Path filePath = Paths.get(AVATAR_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            avatarFile.transferTo(filePath.toFile());

            perfil.setAvatar(storageUrl + "/images/avatar/" + fileName + "?t=" + System.currentTimeMillis());

        }
        perfilRepository.save(perfil);
        usuarioRepository.save(user);
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
