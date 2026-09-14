package it.unibo.risiko.model.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

/** 
 * Event that models the intent of a player to attack another, should not be kept as persistend data.
 * If a persistent copy is needed utilize the visitor to create a specialized implementation.
 *
 * @param attacker A reference to the attacker
 * @param defender A reference to the defender
 * @param attackerStrength troops used by attacker
 * @param defenderStrength troops defending
 * @param attackSource Reference to the territory where the attack came
 * @param attackDestination Reference to the destination territory of the attack
 */
@SuppressFBWarnings("EI_EXPOSE_REP") // ugly but passing the references grants easier control to apply changes
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
