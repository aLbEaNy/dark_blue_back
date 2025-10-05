package personal.darkblueback.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import personal.darkblueback.entities.Perfil;

import java.util.Optional;

public interface PerfilRepository extends MongoRepository<Perfil,String> {
    Optional<Perfil> findByUsername(String username);
    Optional<Perfil> findByNickname(String nickname);
    boolean deleteByNickname(String nickname);
}
