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
        nicknameList.put(2, "ML0-L4");
        nicknameList.put(3, "TR6-S4");
        nicknameList.put(4, "S4N-T1");
    }
}
