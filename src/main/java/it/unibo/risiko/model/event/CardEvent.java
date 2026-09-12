package it.unibo.risiko.model.event;

import java.util.Collection;
import java.util.List;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.player.Player;

/**
 * CardEvent
 */
public record CardEvent(List<Collection<Card>> played, Player owner, int gainedArmies) implements Event{

    @Override
    public <T> T accept(EventVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
