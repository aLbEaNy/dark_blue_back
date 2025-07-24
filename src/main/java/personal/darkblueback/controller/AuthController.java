package personal.darkblueback.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import personal.darkblueback.model.AuthRequest;
import personal.darkblueback.model.AuthResponse;
import personal.darkblueback.model.RegisterRequest;
import personal.darkblueback.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {

            String token = authService.login(request);

            return ResponseEntity.ok(new AuthResponse(token));

    }

    @PostMapping("/register")// El @Valid dispara exception MethodArgumentNotValidException
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request){

        String token = authService.register(request);

        //token de activacion de cuenta
        return ResponseEntity.ok(new AuthResponse(token));
    }


}
