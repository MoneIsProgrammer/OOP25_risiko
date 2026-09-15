package it.unibo.risiko.model.player.strategy;

import java.util.Collection;
import java.util.Map;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

/**
 * Strategy designed to be used by human player, models various methods to build each event incrementally.
 * Trying to generate an event while in a incomplete state throws an {@link IllegalStateException}
 */
public interface HumanStrategy extends PlayerStrategy {
    /**
     * sets the source of an attack.
     * 
     * @param territory where the attack came from
     */
    void attackSource(Territory territory);

    /**
     * sets the destination of an attack.
     * 
     * @param territory what territory the attack targets
     */
    void attackDestination(Territory territory);

    /**
     * sets the number of troops to be used in the attack.
     * 
     * @param strength number of attacker's troops
     */
    void attackStrenght(int strength);

    /**
     * sets the number of troops to reinforce a territory.
     * 
     * @param reinfoceMap Territories to reinforce and troops to add for each one
     * @throws IllegalArgumentException if a value < 1 is passed
     */
    void reinforce(Map<Territory, Integer> reinfoceMap);

    /**
     * sets the source of the movement.
     * 
     * @param territory the source
     * @throws IllegalArgumentException if a value < 1 is passed in a map entry
     */
    void moveSource(Territory territory);

    /**
     * sets the destination of the movement.
     * 
     * @param territory the destination
     */
    void moveDestination(Territory territory);

    /**
     * sets how many troops are to be moved from source to dest.
     * 
     * @param strength number of troops used in the movement
     */
    void moveStrenght(int strength);

    /**
     * sets where troops should be added.
     * 
     * @param placement the mapping of territories and how many troops to add to each
     * @throws IllegalArgumentException if a value < 1 is passed
     */
    void setupPlacement(Map<Territory, Integer> placement);

    /**
     * Used to check if the strategy is finished building {@link AttackEvent}.
     * 
     * @return true if {@link PlayerStrategy#getAttack(it.unibo.risiko.model.player.Player)} can be called
     */
    boolean canCreateAttack();

    /**
     * Used to check if the strategy is finished building {@link ReinforceEvent}.
     * 
     * @return true if {@link PlayerStrategy#getReinforce(it.unibo.risiko.model.player.Player, int)} can be called
     */
    boolean canCreateReinforce();

    /**
     * adds the cards that the player wants to play.
     * 
     * @param combo the combo of cards to be added
     * @throws IllegalArgumentException if the combo does not contain 3 cards
     */
    void cardsToPlay(Collection<Card> combo);

    /**
     * Used to check if the strategy is finished building {@link CardEvent}.
     * 
     * @return true if {@link PlayerStrategy#playCards(java.util.List, it.unibo.risiko.model.player.Player)} can be called
     */
    boolean canPlayCards();

    /** 
     * The specialized metod to get a move event after a conquest, MUST be called after {@link HumanStrategy#moveStrenght(int)},
     * else the minimum movement of 1 army is utilized.
     * 
     * @param sourceID where troops are coming from
     * @param destinationID where troops will end up
     * @param owner the {@link Player} who requested this
     */
    @Override
    MoveEvent getMoveAfterConquest(String sourceID, String destinationID, Player owner);

	/**
	 * Clears the unused argument passed during construction, making a clean slate for next calls
	 */
	void flush();

    /**
     * Used to check if the strategy is finished building {@link MoveEvent}.
     * 
     * @return true if {@link PlayerStrategy#getMove(it.unibo.risiko.model.player.Player)} can be called
     */
    boolean canCreateMove();
}
