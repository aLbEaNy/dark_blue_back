package personal.darkblueback.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/*
    1. consultar cursos -> público
    2. evaluaciones detalle -> requiere autenticación
    3. promedio evaluaciones -> requiere ROLE_PROFESOR
    4. patch (modificar calificación de un estudiante en una evaluación concreta) -> requiere ROLE_ADMIN y ROLE_PROFESOR
 */
@Configuration
@EnableMethodSecurity // Habilita @PreAuthorize y @PostAuthorize
@RequiredArgsConstructor // Para inyectar las dependencias automáticamente porque crea el constructor de propiedades final
//@EnableWebSecurity // No es neceario con Spring Boot 3.x / Spring Security 6.x
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Por defecto, los navegadores bloquean las peticiones entre dominios distintos.
                /*
                    Si no defines los métodos (setAllowedMethods), headers (setAllowedHeaders) ni allowCredentials (setAllowCredentials), tu API:
                    - No aceptará peticiones del frontend.
                    - No permitirá que le envíen tokens en el header Authorization.
                    - No funcionará con herramientas como Postman si intentas autenticación JWT.
                 */
                /*
                ¿Por qué OPTIONS?
                    Cuando haces una petición POST o DELETE con Authorization, el navegador envía una preflight request con método OPTIONS.
                    Si no permites OPTIONS, el navegador bloquea la petición real.
                 */
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:8080")); // Cambia al origen de tu frontend
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true); // Si usas cookies o tokens en headers
                    return config;
                }))

                /*
                CSRF (Cross-Site Request Forgery) es un tipo de ataque donde un sitio malicioso puede hacer que un navegador autenticado
                (por ejemplo, con una cookie activa) haga una petición no deseada a otro sitio en el que el usuario está logueado.
                 */
                /*
                ¿Por qué NO necesitas CSRF en una API REST?
                No usas cookies para la autenticación, sino tokens JWT que van en el header (normalmente en Authorization: Bearer ...).
                No hay formularios web ni sesiones HTML (como en aplicaciones MVC tradicionales).
                En APIs REST las peticiones maliciosas no pueden inyectar headers personalizados como Authorization desde un navegador, por lo que el riesgo de CSRF no aplica.
                 */
                .csrf(csrf -> csrf.disable())
                //.headers(headers -> headers.frameOptions(frame -> frame.disable())) // permitir iframes (para H2)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // permitir iframes (para H2)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/h2-console/**").permitAll() // pública para login/register
                        //.requestMatchers(HttpMethod.GET,"/evaluaciones/*/promedio").hasRole("PROFESOR") // * para un solo argumento variable
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Sin este bean, Spring no sabrá cómo inyectar AuthenticationManager en tus clases.
    // Lo usamos en AuthController
    // No lo necesitas si todo el proceso de autenticación lo maneja Spring automáticamente, como cuando usas formLogin()
    // En una API REST con JWT, donde tú haces la autenticación manualmente y devuelves un token (como tú estás haciendo), sí lo necesitas.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
