package it.unibo.risiko.model.event;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.player.Player;

/**
 * Event that models the playing of card combos and respective army gain,  should not be kept as persistend data.
 * If a persistent copy is needed utilize the visitor to create a specialized implementation.
 * 
 * @param played Various {@link Collection} of Cards played, each collection represent a "Tris"
 * @param owner Reference to the player who made the request
 * @param gainedArmies the number of armies gained
 */

@SuppressFBWarnings("EI_EXPOSE_REP") // ugly but passing the references grants easier control to apply changes
public record CardEvent(List<Collection<Card>> played, Player owner, int gainedArmies) implements Event{

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
