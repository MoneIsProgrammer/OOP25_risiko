package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PhaseController;
import it.unibo.risiko.model.player.Player;

/**
 * This phase allows the current player to place their 
 * armies
 * ReinforcePhase
 */
public class ReinforcePhase implements PhaseController{
    // TODO: this class needs to receive current player

    private final Player player;
    private boolean completed = false;
    
    public void phaseStart() {
        player.reinforce(player.getArmies());

        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    ReinforcePhase(final Player player) {
        this.player = player;
    }

    // FIXME: setupPlacement is enough for this? or getReinforce is?
    /* o devo solo chiamare getReinforce(player.getArmies()) qua?
    private void placeArmies() {
        while(player.getArmies() > 0) {
            player.setupPlacement();
        }
    }*/
}
