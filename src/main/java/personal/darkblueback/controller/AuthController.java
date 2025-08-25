package personal.darkblueback.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.model.*;
import personal.darkblueback.security.JwtService;
import personal.darkblueback.services.AuthService;

import java.util.Random;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<IRestMessage> login(@RequestBody @Valid AuthRequest request) {

        System.out.println("request login--------------------- "+request);
        String token = authService.login(request);

        return ResponseEntity.ok(new IRestMessage(
                0,"envio token de sesion", new AuthResponse(token)
        ));
    }

    @PostMapping("/register")// El @Valid dispara exception MethodArgumentNotValidException
    public ResponseEntity<IRestMessage> register(@RequestBody @Valid RegisterRequest request){

        DataRegister datos = authService.register(request);
        return ResponseEntity.ok(new IRestMessage(
                0,"Registro ok, se envía token y code", datos
        ));
    }
    @PostMapping("/validateCodeActivation")
    public ResponseEntity<Boolean> validateCodeActivation(@RequestBody ActivationData aData) {
        System.out.println("--------  aData vale ----------  "+aData.toString());
        Boolean result = jwtService.isActivationTokenValid(
                aData.getToken(), aData.getCode(), aData.getEmail());
        System.out.println("---------------------------------- "+ result);
        if(result){
            authService.activateAccount(aData.getEmail());
        }
        return ResponseEntity.ok(result);
    }
    @GetMapping("/resendToken")
    public ResponseEntity<IRestMessage> reenviarTokenActivacion(@RequestParam String username, @RequestParam boolean activation) {
        if( activation){
            DataRegister datos = jwtService.generateActivationToken(username);
            return ResponseEntity.ok(new IRestMessage(
                    0,"Se reenvía token y code", datos));
        } else {
            //TODO token normal de sesion
            return ResponseEntity.ok(new IRestMessage(1, "* Error al reenviar el token de activación.",null));
        }
    }
    @GetMapping("/nickname")
    public ResponseEntity<IRestMessage> verificarNickname(@RequestParam String nickname) {
        if (authService.nicknameExist(nickname)) {
            return ResponseEntity.ok(new IRestMessage(0, "* El nickname ya está en uso.",null));
        } else {
            return ResponseEntity.ok(new IRestMessage(1, "* El nickname está disponible.",null));
        }
    }

    @GetMapping("/email")
    public ResponseEntity<IRestMessage> verificarEmail(@RequestParam String email) {
        if (authService.emailExist(email)) {
            return ResponseEntity.ok(new IRestMessage(0, "* El email ya está en uso.",null));
        } else {
            return ResponseEntity.ok(new IRestMessage(1, "* El email está disponible.",null));
        }
    }
}
