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
     * 
     * @param owner Player who request the action
     * @return an {@link Optional} containing the event, if event can't be generated returns {@link Optional#empty()}
     */
    Optional<AttackEvent> getAttack(Player owner); //must pass gamestate so ai can use it, return optional to check 
    // if an event is passed or not either if move is invalid or the bot is done

    /**
     * Used to generate a move event, if unable generates nothing.
     * 
     * @param owner Player who request the action
     * @return an {@link Optional} containing the event, if event can't be generated returns {@link Optional#empty()}
     */
    Optional<MoveEvent> getMove(Player owner);

    /**
     * Used to generate a reinforcement event, if unable generates nothing.
     * 
     * @param owner Player who request the action
     * @param armies How many armies this strategy has to place
     * @return a ReinforceEvent mapping the territories to reinforce and how many armies to add
     */
    ReinforceEvent getReinforce(Player owner, int armies);

    /**
     * Used during the setup phase to making it distinct to {@link PlayerStrategy#getReinforce()}.
     * 
     * @param owner Player who request the action
     * @param startingForces How many armies has to place
     * @return a reinforce event that Maps this step of the setup phase
     */
    ReinforceEvent getSetup(Player owner, int startingForces);

    /**
     * Used to generate a CardEvent, if unable generates nothing.
     * 
     * @param hand the current hand of the player
     * @param owner Player who request the action
     * @return {@link Optional} containing the event, if event can't be generated returns {@link Optional#empty()}
     */
    Optional<CardEvent> playCards(List<Card> hand, Player owner);

    /**
     * The strategy method to ask how many troops to move from source to destination.
     * 
     * @param sourceID the source of the movement
     * @param destinationID the destination of the movement
     * @param owner the player who request this
     * @return the {@link MoveEvent} that rapresents the move of armies, after a conquest,
     */
    MoveEvent getMoveAfterConquest(String sourceID, String destinationID, Player owner);
}
