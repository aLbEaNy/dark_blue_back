package personal.darkblueback.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Item;
import personal.darkblueback.entities.Perfil;
import personal.darkblueback.exception.CustomException;
import personal.darkblueback.model.Stats;
import personal.darkblueback.repository.ItemRepository;
import personal.darkblueback.repository.PerfilRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final PerfilRepository perfilRepository;

    public List<Item> findAllItems(){
        return itemRepository.findAll();
    }
    public Item buyItem(String itemId, String username) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new CustomException("Item no encontrado"));
        Perfil perfil = perfilRepository.findByUsername(username).orElseThrow(()->new CustomException("Usuario no encontrado"));
        Stats _stats = perfil.getStats();
        _stats.setCoins(_stats.getCoins() - item.getPrice());
        if (item.getName().equals("x2Shot") || item.getName().equals("multiShot") || item.getName().equals("laserShot")){
            List arrSpecials = _stats.getSpecials();
            arrSpecials.add(item.getName());
            _stats.setSpecials(arrSpecials);
        }
        perfil.setStats(_stats);
        perfilRepository.save(perfil);
        return item;
    }
}
