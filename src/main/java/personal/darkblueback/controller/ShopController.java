package personal.darkblueback.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import personal.darkblueback.entities.Item;
import personal.darkblueback.security.JwtService;
import personal.darkblueback.services.ItemService;

import java.util.List;


@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ItemService itemService;
    private final JwtService jwtService;

    @GetMapping("/getItems")
    public List<Item> getAllItems (){
        return itemService.findAllItems();
    }
    @GetMapping("/buyItem/{itemId}")
    public Item buyItem(
            @PathVariable String itemId,
            @RequestHeader("Authorization") String authHeader
    ) {
        System.out.println("authHeader: "+authHeader);
        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.extractUsername(token);

        System.out.println("Usuario que compra => " + username);

        return itemService.buyItem(itemId, username);
    }


}
