package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.repository.PerfilRepository;
import personal.darkblueback.services.PerfilService;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;
    private final PerfilRepository perfilRepository;

    @GetMapping("/get/{nickname}")
    public Perfil getPerfilByNickname (@PathVariable String nickname) {
        return perfilService.getPerfilByNickname(nickname);
    }
    @PostMapping("/update")
    public Perfil updatePerfil(@RequestBody Perfil perfil){

        perfilService.savePerfil(perfil);
        return perfil;
    }

    @DeleteMapping("/{nickname}")
    public boolean deletePerfil(@PathVariable String nickname){
       return perfilRepository.deleteByNickname(nickname);
    }



}
