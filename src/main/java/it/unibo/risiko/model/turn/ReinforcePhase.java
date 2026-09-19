package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.Player;

/**
 * This phase allows the current player to place their 
 * armies
 * ReinforcePhase
 */
public class ReinforcePhase {

    private final Player player;
    private final PlayerTurn phase;
    
    ReinforcePhase(final Player player, PlayerTurn phase) {
        this.player = player;
        this.phase = phase;
    }

    // TODO: the player can now place his armies

    void reinforceFinished () {
        phase.advancePhase();
    }
}
