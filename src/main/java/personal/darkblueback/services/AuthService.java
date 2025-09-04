package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.exception.LoginException;
import personal.darkblueback.model.AuthRequest;
import personal.darkblueback.model.DataRegister;
import personal.darkblueback.model.RegisterRequest;
import personal.darkblueback.repository.PerfilRepository;
import personal.darkblueback.repository.UsuarioRepository;
import personal.darkblueback.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilRepository perfilRepository;
    private final PerfilService perfilService;


    public String login (AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            System.out.println(token);
            return token;
        } catch (AuthenticationException e) {
            throw new LoginException("* Usuario o password incorrectos " +e.getMessage());
        }
    }


    public DataRegister register (RegisterRequest request) {
        if(usuarioRepository.findByUsername(request.getUsername()).isPresent()){
            throw new CustomException("*El usuario "+request.getUsername()+" ya existe");
        }

        Usuario newUser = new Usuario(
                null,
                request.getNickname(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "images/user.png",
                "ROLE_USER",
                false
        );
        usuarioRepository.save(newUser);
        System.out.println("Añadido "+ newUser.getUsername() + " a la BD");
        //Crear perfil
        Perfil perfil = perfilService.perfil(newUser);
        perfilRepository.save(perfil);
        //token de activacion de cuenta
        return jwtService.generateActivationToken(request.getUsername());
    }

    public DataRegister reenviarTokenActivacion (String username) {
        return jwtService.generateActivationToken(username); //Token y code
    }

    public boolean nicknameExist (String nickname) {
        return perfilRepository.findByNickname(nickname).isPresent();
    }

    public  boolean emailExist (String email) {
        return usuarioRepository.findByUsername(email).isPresent();
    }

    /**
     * Activa la cuenta del usuario si aún no está activada.
     * @param username correo/username del usuario
     * @return true si se activó en esta operación; false si ya estaba activada
     * @throws CustomException si el usuario no existe
     */
    public boolean activateAccount(String username)
    {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("No existe usuario con username: " + username));

        // Si ya está activado, no hacer cambios
        if (user.isActivate()) {
            return false;
        }

        // Activar y persistir
        user.setActivate(true);
        usuarioRepository.save(user);
        return true;
    }
    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("No existe usuario con username: " + username));
    }
    public void saveUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}

