package it.unibo.risiko.model.event;

import it.unibo.risiko.model.battle.BattleResult;

/**
 * Composition of {@link AttackEvent} where the attack is resolved, should not be kept as persistend data.
 * If a persistent copy is needed utilize the visitor to create a specialized implementation.
 * 
 * @param attack Reference to the attack that originated this
 * @param result Reference to the result of the attack after calculating rolls
 */

public record AttackResultEvent(
    AttackEvent attack,
    BattleResult result
) implements Event {

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
