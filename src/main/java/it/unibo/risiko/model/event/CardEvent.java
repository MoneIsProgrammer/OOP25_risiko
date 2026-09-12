package it.unibo.risiko.model.event;

import java.util.Collection;
import java.util.List;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.player.Player;

/**
 * Event that models the playing of card combos and respective army gain.
 * 
 * @param played combos played
 * @param owner who made the request
 * @param gainedArmies the number of armies gained
 */
public record CardEvent(List<Collection<Card>> played, Player owner, int gainedArmies) implements Event{

    @Override
    public <T> T accept(final EventVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
