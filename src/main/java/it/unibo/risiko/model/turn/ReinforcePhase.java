package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

/**
 * This phase allows the current player to place their 
 * armies
 * ReinforcePhase
 */
public class ReinforcePhase{

    private PlayerTurn turn;
    /* Receive current player from PlayerTurn */
    private Player player = turn.getCurrentPlayer();
    private PlayerStrategy strategy;
    private HumanStrategy humanStrategy;
    private boolean completed = false;
    
    public void phaseStart() {
        placeArmies();

        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    ReinforcePhase(final Player player) {
        this.player = player;
    }

    // FIXME: cosa devo passare a reinforce di human player
    /**
     * Based on whether a player is ai or human, calls methods 
     * to place armies as reinforcements
     */
    private void placeArmies() {
        if (player.isHuman()) {
            strategy = player.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            humanStrategy.reinforce(null);;

        } else {
            player.reinforce(player.getArmies());

        }
    }
}
