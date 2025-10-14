package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.repository.PerfilRepository;
import personal.darkblueback.repository.UsuarioRepository;
import personal.darkblueback.services.PerfilService;
import personal.darkblueback.utils.PasswordUtil;

import java.io.IOException;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;
    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/get/{nickname}")
    public Perfil getPerfilByNickname (@PathVariable String nickname) {
        return perfilService.getPerfilByNickname(nickname);
    }
    @PostMapping("/update")
    public Perfil updatePerfil(@RequestBody Perfil perfil){

        perfilService.savePerfil(perfil);
        return perfil;
    }
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Perfil> updateProfile(
            @RequestParam("username") String username,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) throws IOException {
        Perfil updatedPerfil = perfilService.updateProfile(username, nickname, password, avatarFile);
        return ResponseEntity.ok(updatedPerfil);
    }


    @DeleteMapping("/{nickname}")
    public boolean deletePerfil(@PathVariable String nickname){
       return perfilRepository.deleteByNickname(nickname);
    }



}
