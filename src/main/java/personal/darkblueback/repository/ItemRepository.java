package personal.darkblueback.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import personal.darkblueback.entities.Item;

public interface ItemRepository extends MongoRepository<Item,String> {
}
