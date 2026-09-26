package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.strategy.HumanStrategy;

/**
 * This phase allows the current player to place their 
 * armies.
 */
public class ReinforcePhase {

    private final PlayerTurn turn;
    /* Receive current player from PlayerTurn */
    //private final Player player;
    //private PlayerStrategy strategy;
    //private HumanStrategy humanStrategy;
    private boolean completed;

    /**
     * Default constructor.
     * 
     * @param turn the turn tracker
     */
    ReinforcePhase(final PlayerTurn turn) {
        //this.player = player;
        this.turn = turn;
    }

    /**
     * Starts the phase.
     */
    public void phaseStart() {
        placeArmies();

        completed = true;
    }

    /**
     * @return true if phase is finished
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Based on whether a player is ai or human, calls methods 
     * to place armies as reinforcements.
     */
    private void placeArmies() {
        final var player = turn.getCurrentPlayer();
        if (player.isHuman()) {
            final var strategy = player.getStrategy();
            final var humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateReinforce()) {
                player.reinforce(player.getArmies());
            }
        } else {
            player.reinforce(player.getArmies());

        }
    }
}
