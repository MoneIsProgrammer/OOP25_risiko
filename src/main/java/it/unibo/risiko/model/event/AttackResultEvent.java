package it.unibo.risiko.model.event;

import it.unibo.risiko.model.battle.BattleResult;

/**
 * Composition of {@link AttackEvent} where the attack is resolved, should not be kept as persistent data.
 * 
 * @param attack attack that originated this
 * @param attackerLosses troops lost by attacker
 * @param defenderLosses troops lost by defender
 * @param conquered true if attacker conquered the territory
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
