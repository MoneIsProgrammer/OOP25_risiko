package it.unibo.risiko.model.player.strategy;

import java.util.List;
import java.util.Optional;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.player.Player;

/**
 * General strategy implemetation giving the methods to get the result of an action.
 */
public interface PlayerStrategy {

    /**
     * Used to generate an attack event, if unable generates nothing.
     * @param owner TODO
     * @return an {@link Optional} containing the event, if event can't be generated returns {@link Optional#empty()}
     */
    Optional<AttackEvent> getAttack(Player owner); //must pass gamestate so ai can use it, return optional to check 
    // if an event is passed or not either if move is invalid or the bot is done

    /**
     * Used to generate a move event, if unable generates nothing.
     * @param owner TODO
     * @return an {@link Optional} containing the event, if event can't be generated returns {@link Optional#empty()}
     */
    Optional<MoveEvent> getMove(Player owner);

    /**
     * Used to generate a reinforcement event, if unable generates nothing.
     * @param owner TODO
     * @param armies TODO
     * @return a ReinforceEvent mapping the territories to reinforce and how many armies to add
     */
    ReinforceEvent getReinforce(Player owner, int armies);

    ReinforceEvent getSetup(Player owner, int startingForces);

    Optional<CardEvent> playCards(List<Card> hand, Player owner);
}
