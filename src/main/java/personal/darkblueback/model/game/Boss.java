package personal.darkblueback.model.game;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Boss {
    private Map<Integer, String> nicknameList;
    private String avatarBoss;

    public Boss() {
        nicknameList = new HashMap<>();
        nicknameList.put(1, "PMR-69");
        nicknameList.put(2, "ML0-LA");
        nicknameList.put(3, "IA-BOSS3");
        nicknameList.put(4, "IA-BOSS4");
    }
}
