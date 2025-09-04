package personal.darkblueback.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import personal.darkblueback.entities.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
}
