package personal.darkblueback.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.model.*;
import personal.darkblueback.security.JwtService;
import personal.darkblueback.services.AuthService;
import personal.darkblueback.services.AvatarService;
import personal.darkblueback.services.GmailService;
import personal.darkblueback.services.PerfilService;
import personal.darkblueback.utils.PasswordUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final GmailService gmailService;
    private final PerfilService perfilService;
    private final AvatarService avatarService;

    @Value( "${google.CLIENT_ID}")
    private String CLIENT_ID;
    @Value( "${google.CLIENT_SECRET}")
    private String CLIENT_SECRET;
    private String REDIRECT_URI = "postmessage"; // obligatorio para popup OAuth


    @PostMapping("/google")
    public ResponseEntity<IRestMessage> googleLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 1. Intercambiar code por tokens
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                Map.class
        );

        String idToken = (String) tokenResponse.getBody().get("id_token");
        System.out.println("idToken Google. ------------------> "+idToken);

        // 2. Decodificar el id_token (JWT) → datos usuario
        String[] parts = idToken.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        Map<String, Object> payload;
        try {
            payload = new ObjectMapper().readValue(payloadJson, Map.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok(new IRestMessage(400,"Error parsing Google ID token payload",null));
        }

        // Datos básicos del usuario
        String email = (String) payload.get("email");
        String avatarUrlGoogle = (String) payload.get("picture");
        String avatar = avatarService.saveAvatarFromUrl(avatarUrlGoogle, email);
        String name = (String) payload.get("name");
        System.out.println("email Google. ------------------> "+email);
        System.out.println("picture Google. ------------------> "+avatarUrlGoogle);
        System.out.println("name Google. ------------------> "+name);

        // 3. ... JWT o guardar usuario en DB
        if (!authService.emailExist(email)){
            // Se registra por primera vez y se hace login
            name = name.replace(" ", "_");
            while (authService.nicknameExist(name.substring(0,9))){
                name = name.substring(0,6) + (int)(Math.random()*1000);
            }
            Usuario newUser = new Usuario(
                    null,
                    name.substring(0,9),
                    email,
                    PasswordUtil.hashPassword(" "),
                    true
            );
            authService.saveUsuario(newUser);
            String token = jwtService.generateToken(newUser);
            System.out.println("----------- token ------------>  "+token);
            Perfil perfil = perfilService.newPerfil(newUser, avatar);

            return ResponseEntity.ok(new IRestMessage(0,"envio token de sesion", new AuthResponse(token,perfil)));
        } else {
            Usuario user = authService.getUsuarioByUsername(email);
            user.setActivate(true);//por si acaso no lo hizo antes
            Perfil _perfil = perfilService.getPerfilByUsername(user.getUsername());

            authService.saveUsuario(user);
            return ResponseEntity.ok(new IRestMessage(0,"envio token de sesion",
                    new AuthResponse(jwtService.generateToken(user), _perfil)));
        }
    }
    @GetMapping("/avatar/{userId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String userId) throws IOException {
        Path filePath = Paths.get("avatars", userId + ".png");
        if (Files.exists(filePath)) {
            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/login")
    public ResponseEntity<IRestMessage> login(@RequestBody @Valid AuthRequest request) {
        System.out.println("request login--------------------- " + request);
        String token = authService.login(request);
        System.out.println("token login--------------------- " + token);
        Usuario user = authService.getUsuarioByUsername(request.getUsername());
        Perfil perfil = perfilService.getPerfilByUsername(request.getUsername());


        return ResponseEntity.ok(new IRestMessage(
                0, "envio token de sesion", new AuthResponse(token, perfil)
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<IRestMessage> register(@RequestBody @Valid RegisterRequest request) {
        DataRegister datos = authService.register(request);
        System.out.println("--------  datos codeActivation ----------  " + datos.getCodeActivation());
        // Envía el código al email
        try {
            gmailService.sendVerificationCode(request.getUsername(), datos.getCodeActivation());
        } catch (MessagingException e) {
            throw new CustomException(e.getMessage());
        }
        return ResponseEntity.ok(new IRestMessage(
                0, "Registro ok, se envía token y code", datos
        ));
    }

    @PostMapping("/validateCodeActivation")
    public ResponseEntity<Boolean> validateCodeActivation(@RequestBody ActivationData aData) {
        System.out.println("--------  aData vale ----------  " + aData);
        Boolean result = jwtService.isActivationTokenValid(
                aData.getToken(), aData.getCode(), aData.getEmail()
        );
        System.out.println("---------------------------------- " + result);
        if (result) {
            authService.activateAccount(aData.getEmail());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resendToken")
    public ResponseEntity<IRestMessage> reenviarTokenActivacion(@RequestParam String username, @RequestParam(required = false) boolean activation) {
        if (Boolean.TRUE.equals(activation)) {// solo si viene y es true
            DataRegister datos = jwtService.generateActivationToken(username);//token y code
            // Envía el código al email
            try {
                gmailService.sendVerificationCode(username, datos.getCodeActivation());
            } catch (MessagingException e) {
                throw new CustomException(e.getMessage());
            }
            return ResponseEntity.ok(new IRestMessage(
                    0, "Se reenvía token y code", datos));
        } else {
            //Compruebo usuario y si existe y devuelvo token
            Usuario user = authService.getUsuarioByUsername(username);
            String token = jwtService.generateToken(user);
            Map<String, String> datos = new HashMap<>();
            datos.put("token", token);
            return ResponseEntity.ok(new IRestMessage(0,"se reenvía token de sesion", datos));
        }
    }

    @GetMapping("/nickname")
    public ResponseEntity<IRestMessage> verificarNickname(@RequestParam String nickname) {
        if (authService.nicknameExist(nickname)) {
            return ResponseEntity.ok(new IRestMessage(0, "* El nickname ya está en uso.", null));
        } else {
            return ResponseEntity.ok(new IRestMessage(1, "* El nickname está disponible.", null));
        }
    }

    @GetMapping("/email")
    public ResponseEntity<IRestMessage> verificarEmail(@RequestParam String email) {
        if (authService.emailExist(email)) {
            return ResponseEntity.ok(new IRestMessage(0, "* El email ya está en uso.", null));
        } else {
            return ResponseEntity.ok(new IRestMessage(1, "* El email está disponible.", null));
        }
    }
    @DeleteMapping()
    public boolean deleteAccount(@RequestParam String username){
        return authService.deleteAccount(username);
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<IRestMessage> forgotPassword(@RequestParam String email) {
        try {
            if(!authService.emailExist(email))
                return ResponseEntity.ok(new IRestMessage(1, "El correo" +email+ "no está registrado en Dark Blue.", null));
            else {
                Usuario user = authService.getUsuarioByUsername(email);
                String pass = "dB@1miss"+(int)(Math.random()*1000);
                user.setPassword(PasswordUtil.hashPassword(pass));
                authService.saveUsuario(user);
                gmailService.sendResetPassword(user.getUsername(), pass);
                return ResponseEntity.ok(new IRestMessage(0, "ok", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new IRestMessage(2, " Ha ocurrido un error al resetar la contraseña... Inténtalo de nuevo más tarde", null));
        }


    }
}
