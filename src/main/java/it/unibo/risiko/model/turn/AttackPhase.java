package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.strategy.HumanStrategy;

/**
 * This phase allows the current player to attack.
 * 
 */
public class AttackPhase {

    private final PlayerTurn turn;
    private boolean isCompleted;

    AttackPhase(final PlayerTurn turn) {
        this.turn = turn;
    }

    /**
     * checks if the phase can be started.
     */
    public void phaseStart() {
        canAttack();

        isCompleted = true;
    }

    /**
     * @return true if it can be completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Based on whether a player is ai or human, allows player to attack other 
     * player's territories.
     */
    void canAttack() {
        final var player = turn.getCurrentPlayer();
        if (!player.isHuman()) {
            player.attack();
        } else {
            final var strategy = player.getStrategy();
            final var humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateAttack()) {
                player.attack();
            }
        }
    }
}
