package it.unibo.risiko.model.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

/**
 * Event that models the movement of troops from a owned territory to another, should not be kept as persistend data.
 * If a persistent copy is needed utilize the visitor to create a specialized implementation.
 * 
 * @param player Reference to the player that generated event
 * @param sourceTerritory Reference to the territory from which troops came from
 * @param destinationTerritory Reference to the territory where troops will end up
 * @param troopsMoved numbere of troops moved
 */

@SuppressFBWarnings("EI_EXPOSE_REP") // ugly but passing the references grants easier control to apply changes
public record MoveEvent(
    Player player,
    Territory sourceTerritory,
    Territory destinationTerritory,
    int troopsMoved
) implements Event {

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
