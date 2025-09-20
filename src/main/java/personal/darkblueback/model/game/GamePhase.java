package personal.darkblueback.model.game;

public enum GamePhase {
    JOINED,
    PLACEMENT,   // ambos jugadores colocando submarinos
    READY1,
    READY2,
    BATTLE, // turno de disparos
    END     // partida terminada
}
