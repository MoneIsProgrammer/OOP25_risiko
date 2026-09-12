package it.unibo.risiko.model.player.strategy;

import java.util.Collection;
import java.util.Map;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.map.Territory;

/**
 * Strategy designed to be used by human player, models various methods to build each event.
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
     */
    void reinforce(Map<Territory, Integer> reinfoceMap);

    /**
     * sets the source of the movement.
     * 
     * @param territory the source
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

    void setupPlacement(Map<Territory, Integer> placement);

    boolean canCreateAttack();

    boolean canCreateReinforce();

    void cardsToPlay(Collection<Card> combo);

    boolean canPlayCards();
}
