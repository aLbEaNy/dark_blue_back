package personal.darkblueback.model.game;

public enum GamePhase {
    JOINED,
    PLACEMENT,   // ambos jugadores colocando submarinos
    WAITING,
    BATTLE, // turno de disparos
    END     // partida terminada
}
