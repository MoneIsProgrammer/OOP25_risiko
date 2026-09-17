package it.unibo.risiko.model.player;

import java.util.List;
import java.util.Optional;

import it.unibo.risiko.model.common.Identifiable;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

/**
 * The general implementation of a Player, gives the general methods for the basic actions and methods.
 * to ease the implementation of human and bot Players.
 * It's designed so that each player behavior is differentiated by the Strategy Pattern,
 * meaning for each different behavior and external {@link PlayerStrategy} implementation is required.
 */
public interface Player extends Identifiable {
    /**
     * Returns the Strategy used by the player.
     * 
     * @return the strategy used by the player, useful if the strategy requires succesive method calls to build the event
     */
    PlayerStrategy getStrategy();

    /**
     * If the player is controlled by a human returns true, else returns false.
     * 
     * @return true if the player is human controlled
     */
    boolean isHuman();

    /**
     * Used to get the next attack that the player wants to take.
     * 
     * @return an {@link Optional} containing the event if it can be generated, {@link Optional#empty()} otherwise
     */
    Optional<AttackEvent> attack();

    /**
     * Used to get the transfer of troops from a territory to another.
     * 
     * @return an {@link Optional} containing the event if it can be generated, {@link Optional#empty()} otherwise
     */
    Optional<MoveEvent> move();

    /**
     * Used to get the reinforcement to various territories.
     * 
     * @param armies How many armies to place
     * @return a {@link ReinforceEvent} describing the reinforce, it's not an {@link Optional} as this is a forced move
     */
    ReinforceEvent reinforce(int armies);

    /**
     * Creates reinforce events with assigning at maximum 3 troops at a time,
     * useful during the setup phase as it knows how many troops has to place.
     * 
     * @return a ReinfoceEvent
     */
    ReinforceEvent setupPlacement();

    /**
     * Getter for the player name.
     * 
     * @return the name of the player
     */
    String getName();

    /**
     * Getter for the PlayerColor.
     * 
     * @return the color of the player
     */
    RisikoColors getColor();

    /**
     * Setter for the armies to place during the setupPhase,
     * useful to ovveride the default value assigned by {@link PlayerFactory}.
     * 
     * @param armies number of armies for the setup phase
     */
    void setArmies(int armies);

    /**
     * Used to get the {@link Cards} to play to get additional reinforcements.
     * 
     * @return an {@link Optional} containing the event if it can be generated, {@link Optional#empty()} otherwise
     */
    Optional<CardEvent> playCard();

    /**
     * Adds a {@link Card} tho the player hand.
     * 
     * @param card the  {@link Card} to add to the player's hand
     */
    void addCard(Card card);

    /**
     * Used to get the transfer of troops from a territory to another, 
     * speficially this generates a MoveEvent rapresenting che transfer of troops from attacking territory to the conquered one.
     * 
     * @param sourceID the attacking territory from where the troops come from
     * @param destinationID the conquered territory where the troops will end up
     * @return an {@link MoveEvent}, this is a forced event where at worst just 1 troop must be moved to occupy the destination
     */
    MoveEvent getMoveAfterConquest(String sourceID, String destinationID);

    /**
     * Gets the cards in the player's hand.
     * 
     * @return A list of card in the player hand
     */
    List<Card> getHand();

}
