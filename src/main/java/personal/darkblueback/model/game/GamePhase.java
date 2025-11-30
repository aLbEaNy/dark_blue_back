package personal.darkblueback.model.game;

public enum GamePhase {
    EXIT,
    JOINED,
    WAITING,
    PLACEMENT,   // ambos jugadores colocando submarinos
    BATTLE, // turno de disparos
    END,    // partida terminada
    SPECIAL
}
