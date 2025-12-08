package personal.darkblueback.services;

import org.springframework.stereotype.Service;
import personal.darkblueback.model.game.Board;
import personal.darkblueback.model.game.Shot;
import personal.darkblueback.model.game.Submarine;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final String[] letters = "ABCDEFGHIJ".split("");

    // Estado por jugador (clave: gameId:nickname)
    private final Map<String, EstadoIA> estados = new HashMap<>();

    // Clase interna para guardar el estado de la IA
    private static class EstadoIA {
        List<String> targetHits = new ArrayList<>();
        String lastDirection = null; // "H" o "V"
    }

    public List<Shot> computeShots(Board board, String special, String gameId, String nickname) {
        List<Shot> shots = new ArrayList<>();
        EstadoIA estado = estados.computeIfAbsent(gameId + ":" + nickname, k -> new EstadoIA());

        if ("laserShot".equals(special)) {
            List<String> laserPositions = getLaserPositions(board);
            for (String pos : laserPositions) {
                shots.add(fireSingle(board, pos, estado));
            }
        } else if ("multiShot".equals(special)) {
            int shotsCount = 5; // puedes ajustar
            for (int i = 0; i < shotsCount; i++) {
                shots.add(fireSingle(board, "", estado));
            }
        } else {
            shots.add(fireSingle(board, "", estado));
        }

        return shots;
    }

    private Shot fireSingle(Board board, String laserPos, EstadoIA estado) {
        // Si todos los submarinos están hundidos
        if (board.getSubmarines().stream().allMatch(Submarine::getIsDestroyed)) {
            return new Shot("NONE", "ALL_DESTROYED");
        }

        // Decidir posición
        String pos;
        if (!laserPos.isEmpty()) {
            pos = laserPos;
        } else if (estado.targetHits.isEmpty()) {
            pos = smartRandomPosition(board);
        } else if (estado.targetHits.size() == 1) {
            List<String> candidates = getAdjacent(estado.targetHits.get(0), board);
            pos = pickRandom(candidates);
        } else {
            pos = fireAlongLine(board, estado);
        }

        // Aplicar el resultado
        List<Submarine> updatedSubs = board.getSubmarines().stream()
                .map(sub -> {
                    List<Boolean> touched = new ArrayList<>(sub.getIsTouched());
                    int idx = sub.getPositions().indexOf(pos);
                    if (idx != -1) {
                        touched.set(idx, true);
                        boolean destroyed = touched.stream().allMatch(Boolean::booleanValue);
                        return new Submarine(
                                sub.getId(),
                                sub.getTipo(),
                                sub.getSizeSub(),
                                sub.getPositions(),
                                touched,
                                destroyed,
                                sub.getIsHorizontal()
                        );
                    }
                    return sub;
                })
                .collect(Collectors.toList());

        Optional<Submarine> hitSubOpt = updatedSubs.stream()
                .filter(sub -> sub.getPositions().contains(pos))
                .findFirst();

        String result = hitSubOpt.isPresent() ? "HIT" : "MISS";
        if (hitSubOpt.isPresent() && hitSubOpt.get().getIsDestroyed()) {
            result = "DESTROYED";
            estado.targetHits.clear();
            estado.lastDirection = null;
        } else if (hitSubOpt.isPresent()) {
            if (!estado.targetHits.contains(pos)) estado.targetHits.add(pos);
            if (estado.targetHits.size() >= 2) estado.lastDirection = getDirection(estado.targetHits);
        }

        board.setSubmarines(updatedSubs);
        board.getShots().add(new Shot(pos, result));

        return new Shot(pos, result);
    }

    // -----------------------
    // UTILIDADES
    // -----------------------

    private List<String> allPositions() {
        List<String> res = new ArrayList<>();
        for (String r : letters) {
            for (int c = 1; c <= 10; c++) res.add(r + c);
        }
        return res;
    }

    private String smartRandomPosition(Board board) {
        Set<String> used = board.getShots().stream().map(Shot::getPosition).collect(Collectors.toSet());
        List<String> free = allPositions().stream().filter(p -> !used.contains(p)).collect(Collectors.toList());
        if (free.isEmpty()) return "NONE";
        Collections.shuffle(free);
        return free.get(0);
    }

    private List<String> getAdjacent(String pos, Board board) {
        char row = pos.charAt(0);
        int col = Integer.parseInt(pos.substring(1));
        int idx = Arrays.asList(letters).indexOf(String.valueOf(row));

        List<String> candidates = new ArrayList<>();
        if (idx > 0) candidates.add(letters[idx - 1] + String.valueOf(col));
        if (idx < letters.length - 1) candidates.add(letters[idx + 1] + String.valueOf(col));
        if (col > 1) candidates.add(row + String.valueOf(col - 1));
        if (col < 10) candidates.add(row + String.valueOf(col + 1));

        Set<String> used = board.getShots().stream().map(Shot::getPosition).collect(Collectors.toSet());
        return candidates.stream().filter(c -> !used.contains(c)).collect(Collectors.toList());
    }

    private String fireAlongLine(Board board, EstadoIA estado) {
        List<Character> rows = estado.targetHits.stream().map(h -> h.charAt(0)).toList();
        List<Integer> cols = estado.targetHits.stream().map(h -> Integer.parseInt(h.substring(1))).toList();

        Set<String> used = board.getShots().stream().map(Shot::getPosition).collect(Collectors.toSet());
        List<String> candidates = new ArrayList<>();

        if ("H".equals(estado.lastDirection)) {
            char row = rows.get(0);
            int min = Collections.min(cols);
            int max = Collections.max(cols);
            if (min > 1) candidates.add(row + String.valueOf(min - 1));
            if (max < 10) candidates.add(row + String.valueOf(max + 1));
        } else if ("V".equals(estado.lastDirection)) {
            int col = cols.get(0);
            int minIdx = rows.stream().map(r -> Arrays.asList(letters).indexOf(String.valueOf(r))).min(Integer::compareTo).orElse(0);
            int maxIdx = rows.stream().map(r -> Arrays.asList(letters).indexOf(String.valueOf(r))).max(Integer::compareTo).orElse(letters.length - 1);
            if (minIdx > 0) candidates.add(letters[minIdx - 1] + col);
            if (maxIdx < letters.length - 1) candidates.add(letters[maxIdx + 1] + col);
        }

        List<String> free = candidates.stream().filter(c -> !used.contains(c)).collect(Collectors.toList());
        if (free.isEmpty()) return smartRandomPosition(board);
        return pickRandom(free);
    }

    private String pickRandom(List<String> list) {
        if (list == null || list.isEmpty()) return "NONE";
        Collections.shuffle(list);
        return list.get(0);
    }

    private String getDirection(List<String> hits) {
        List<Character> rows = hits.stream().map(h -> h.charAt(0)).toList();
        boolean horizontal = rows.stream().allMatch(r -> r.equals(rows.get(0)));
        return horizontal ? "H" : "V";
    }

    private List<String> getLaserPositions(Board board) {
        Set<String> used = board.getShots().stream().map(Shot::getPosition).collect(Collectors.toSet());
        boolean horizontal = Math.random() < 0.5;
        List<String> positions = new ArrayList<>();

        if (horizontal) {
            String row = letters[new Random().nextInt(letters.length)];
            for (int i = 1; i <= 10; i++) positions.add(row + i);
        } else {
            int col = new Random().nextInt(10) + 1;
            for (String r : letters) positions.add(r + col);
        }

        return positions.stream().filter(p -> !used.contains(p)).collect(Collectors.toList());
    }
}
