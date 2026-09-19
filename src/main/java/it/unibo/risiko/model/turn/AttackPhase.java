package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;

/**
 * This phase allows the current player to attack
 * AttackPhase
 */
public class AttackPhase {

    private final PlayerTurn phase;

    AttackPhase (final PlayerTurn phase) {
        this.phase = phase;
    }
    // TODO: Attack Implementation or already method exists?

    void attackFinished() {
        phase.advancePhase();
    }

}
