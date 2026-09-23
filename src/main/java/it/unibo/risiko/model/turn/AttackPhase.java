package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

/**
 * This phase allows the current player to attack
 * AttackPhase
 */
public class AttackPhase{

    private final PlayerTurn turn;
    private Player player;
    private PlayerStrategy strategy;
    private HumanStrategy humanStrategy;
    private boolean isCompleted = false;

    public void phaseStart() {
        canAttack();

        isCompleted = true;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    AttackPhase(final PlayerTurn turn) {
        this.turn = turn;
    }
    
    /**
     * Based on whether a player is ai or human, allows player to attack other 
     * player's territories
     */
    void canAttack() {
        player = turn.getCurrentPlayer();
        if (!(player.isHuman())) {
            player.attack();
        } else {
            strategy = player.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateAttack()) {
                player.attack();
            }
        }
    }

}
