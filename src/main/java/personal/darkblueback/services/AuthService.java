package personal.darkblueback.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.exception.LoginException;
import personal.darkblueback.model.AuthRequest;
import personal.darkblueback.model.AuthResponse;
import personal.darkblueback.model.RegisterRequest;
import personal.darkblueback.repository.UsuarioRepository;
import personal.darkblueback.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public String login (AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            return token;
        } catch (AuthenticationException e) {
            throw new LoginException("* Usuario o password incorrectos " +e.getMessage());
        }
    }


    public String register (RegisterRequest request) {
        if(usuarioRepository.findByUsername(request.getUsername()).isPresent()){
            throw new CustomException("*El usuario "+request.getUsername()+" ya existe");
        }

        Usuario newUser = new Usuario(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );
        usuarioRepository.save(newUser);
        System.out.println("Añadido "+ newUser.getUsername() + " a la BD");
        //token de activacion de cuenta
        return jwtService.generateToken(newUser);
    }
}

