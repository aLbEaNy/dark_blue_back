package personal.darkblueback.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import personal.darkblueback.entities.Game;

import java.util.Optional;

public interface GameRepository extends MongoRepository<Game,String> {
    void deleteByPlayer1(String player1);

    Optional<Game> findFirstByOnlineTrueAndPlayer1NotNullAndPlayer2IsNull();

}
