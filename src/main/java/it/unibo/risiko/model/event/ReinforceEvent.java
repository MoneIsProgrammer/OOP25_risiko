package it.unibo.risiko.model.event;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

/**
 * Event that models the reinforcement of troops in owned territories, should not be kept as persistend data.
 * If a persistent copy is needed utilize the visitor to create a specialized implementation.
 * 
 * @param player Reference to the player that generated the event
 * @param reinforcement maps territory to number of troops to add
 */

@SuppressFBWarnings("EI_EXPOSE_REP") // ugly but passing the references grants easier control to apply changes
public record ReinforceEvent(
    Player player,
    Map<Territory, Integer> reinforcement 
) implements Event {

    /**
     * Constructor for ReinforceEvent.
     * 
     * @param player that generates this event
     * @param reinforcement map of territory with troops to add to each one
     */
    public ReinforceEvent(final Player player, final Map<Territory, Integer> reinforcement) {
        this.player = player;
        this.reinforcement = Map.copyOf(reinforcement);
    }

    /**
     * Calculates how many troops are added in this event.
     * 
     * @return the total troops that are gained in the reinforcement
     */
    public int totalReinforcement() {
        return reinforcement.values().stream().mapToInt(i -> i).sum();
    }

    /**
     * Used to get a copy of the map of territory and reinforcements.
     * 
     * @return an immutable copy of the territories and their number of additional troops
     */
    @Override
    public Map<Territory, Integer> reinforcement() {
        return Map.copyOf(reinforcement);
    }

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
