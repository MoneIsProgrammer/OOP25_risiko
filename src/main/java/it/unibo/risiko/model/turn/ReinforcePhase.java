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

    private final PlayerTurn turn;
    /* Receive current player from PlayerTurn */
    private Player player;
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

    ReinforcePhase(final PlayerTurn turn) {
        this.turn = turn;
    }

    /**
     * Based on whether a player is ai or human, calls methods 
     * to place armies as reinforcements
     */
    private void placeArmies() {
        player  = turn.getCurrentPlayer();
        if (player.isHuman()) {
            strategy = player.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateReinforce()) {
                player.reinforce(player.getArmies());
            }
        } else {
            player.reinforce(player.getArmies());

        }
    }
}
