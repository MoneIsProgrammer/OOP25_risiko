package it.unibo.risiko.model.map;

import it.unibo.risiko.model.common.Identifiable;
import java.util.Set;

/**
 * A group of territories that gives extra armies to whoever owns all of them.
 * The owner isn't stored here, {@link GameMap#getContinentOwner(String)} works it out
 * from the territories.
 */
public interface Continent extends Identifiable {

    /**
     * Name to show on screen.
     *
     * @return the name of the continent
     */
    String getName();

    /**
     * The territories that are part of the continent.
     *
     * @return their ids in an unmodifiable set
     */
    Set<String> getTerritoryIds();

    /**
     * Extra armies you get every turn if you own the whole continent.
     *
     * @return the bonus in armies
     */
    int getBonusArmies();
}
