package it.unibo.risiko.model.event;

import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

/** 
 * Event that models the intent of a player to attack another, sould not be kept as persistend data.
 *
 * @param attacker the attacker
 * @param defender the victim
 * @param attackerStrength troops used by attacker
 * @param defenderStrength troops defending
 * @param attackSource territory where the attack came
 * @param attackDestination destination territory of attack
 */
public record AttackEvent(
    Player attacker,
    Player defender,
    int attackerStrength,
    int defenderStrength,
    Territory attackSource,
    Territory attackDestination
) implements Event {

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
