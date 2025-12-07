package personal.darkblueback.services;

import org.springframework.stereotype.Service;
import personal.darkblueback.entities.Game;
import personal.darkblueback.model.gameDTO.Special;

@Service
public class SpecialService {

    public Game incrementSpecialCounter(Game game, String player) {
        System.out.println("player: " + player);
        Special special = player.equals("player1") ? game.getSpecialPlayer1() : game.getSpecialPlayer2();
        System.out.println("incrementSpecialCounter: special1=" + special.getSpecial1() + ", counter1=" + special.getCounter1());

        if (special.getSpecial1() != null && !special.getSpecial1().isEmpty()) {
            special.setCounter1(special.getCounter1() + 1);
            if (checkReady(special.getSpecial1(), special.getCounter1())) {
                special.setActiveSpecial1(true);
                special.setCounter1(0);
            }
        }

        if (special.getSpecial2() != null && !special.getSpecial2().isEmpty()) {
            special.setCounter2(special.getCounter2() + 1);
            if (checkReady(special.getSpecial2(), special.getCounter2())) {
                special.setActiveSpecial2(true);
                special.setCounter2(0);
            }
        }
        if (player.equals("player1")) {
            game.setSpecialPlayer1(special);
        } else {
            game.setSpecialPlayer2(special);
        }
        return game;
    }

    private boolean checkReady(String name, int counter) {
        System.out.println("checkReady -------------> " + name);
        return switch (name) {
            case "x2Shot" -> counter == 3;
            case "multiShot" -> counter == 4;
            case "laserShot" -> counter == 6;
            default -> false;
        };
    }
}
