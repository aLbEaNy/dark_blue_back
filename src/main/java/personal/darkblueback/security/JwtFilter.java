package personal.darkblueback.security;


//@Component
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
/*
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;

        // Comprobar que el header Authorization existe y empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        try {
            // Extraemos el username del token
            username = jwtService.extractUsername(jwtToken);

            // Si el usuario no está autenticado aún
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargamos detalles del usuario desde la BD o donde tengas guardados los usuarios
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validar el token contra el usuario
                if (jwtService.isTokenValid(jwtToken, userDetails)) {

                    // Extraer rol del token
                    Claims claims = jwtService.extractAllClaims(jwtToken);
                    String role = claims.get("role", String.class);

                    // Crear authorities con el rol extraído
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    // Crear token de autenticación para Spring Security
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    // Establecer el contexto de seguridad con la autenticación creada
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Puedes loguear o manejar errores si quieres
            System.out.println("JWT no válido: " + e.getMessage());
        }

        // Continuar cadena de filtros
        filterChain.doFilter(request, response);
    }
}

 */
