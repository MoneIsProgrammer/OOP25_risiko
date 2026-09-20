package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PhaseController;
import it.unibo.risiko.model.player.Player;

/**
 * This phase allows the current player to attack
 * AttackPhase
 */
public class AttackPhase implements PhaseController{

    private final Player player;

    public void phaseStart() {
        player.attack();
    }

    AttackPhase (final Player player) {
        this.player = player;
    }
    // TODO: Attack Implementation or already method exists? is it player.attack() or player.strategy.attack?
    /*void canAttack() {
        player.attack();
    }*/

}
