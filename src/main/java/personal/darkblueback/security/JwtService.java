package personal.darkblueback.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Usuario;
import personal.darkblueback.model.DataRegister;

import java.security.Key;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class JwtService {


    private static final String SECRET_KEY = "clave_super_secreta_que_debe_ser_larga_y_segura_para_el_hmac_sha_okokokokok";

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    //Token normal para sesion/autenticacion
    public String generateToken(Usuario user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8 horas
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // Token para activación de cuenta con código (10 minutos de validez)
    public DataRegister generateActivationToken(String username) {

        //Codigo de 6 dígitos aleatorio
        int code = 100000 + new Random().nextInt(900000);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("activationCode", String.valueOf(code))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutos
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return new DataRegister(token, String.valueOf(code));
    }

    //Extraer usuario
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer código de activación del token
    public String extractActivationCode(String token) {
        return extractClaim(token, claims -> claims.get("activationCode", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Validar token de sesión
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

    // Validar token de activación (usuario y código)
    public boolean isActivationTokenValid(String token, String code, String username) {
        try {
            final String _username = extractUsername(token);
            final String _code = extractActivationCode(token);
            System.out.println("--------------- jwtService lo que recibe : ---- "+ username +"     " +code);
            System.out.println("--------------- jwtService lo que extrae : ---- "+ _username +"     " +_code);
            return _username.equals(username) && _code.equals(code);
        } catch (Exception e) {
            return false;
        }
    }

    // Comprobar si el token está expirado
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }


    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
