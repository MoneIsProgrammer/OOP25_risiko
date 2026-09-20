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

    private PlayerTurn turn;
    private Player player = turn.getCurrentPlayer();
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

    AttackPhase (final Player player) {
        this.player = player;
    }
    // FIXME: how do i get the player to attack, what values do I pass to the methods for human player?
    /**
     * Based on whether a player is ai or human, allows player to attack other 
     * player's territories
     */
    void canAttack() {
        if (!(player.isHuman())) {
            player.attack();
        } else {
            strategy = player.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            humanStrategy.attackSource(null);
            humanStrategy.attackDestination(null);
            humanStrategy.attackStrenght(0);
        }
    }

}
