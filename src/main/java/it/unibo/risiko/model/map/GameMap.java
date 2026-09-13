package it.unibo.risiko.model.map;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * The game map: territories, continents and which territories are neighbours.
 * The other packages ask this and only this about the geography of the game.
 */
public interface GameMap {

    /**
     * All the territories in loading order.
     *
     * @return the territories in an unmodifiable collection
     */
    Collection<Territory> getTerritories();

    /**
     * Finds a territory by id.
     *
     * @param id the id of the territory
     * @return the territory with that id
     * @throws IllegalArgumentException if the id does not exist
     */
    Territory getTerritory(String id);

    /**
     * All the continents in loading order.
     *
     * @return the continents in an unmodifiable collection
     */
    Collection<Continent> getContinents();

    /**
     * Finds a continent by id.
     *
     * @param id the id of the continent
     * @return the continent with that id
     * @throws IllegalArgumentException if the id does not exist
     */
    Continent getContinent(String id);

    /**
     * Tells if two territories are neighbours.
     *
     * @param first the first territory
     * @param second the second territory
     * @return true if they are neighbours
     * @throws IllegalArgumentException if the first id does not exist
     */
    boolean areAdjacent(String first, String second);

    /**
     * All the territories of a player.
     *
     * @param playerId the id of the player
     * @return their territories, empty if they have none
     */
    Set<Territory> getTerritoriesOf(String playerId);

    /**
     * Who owns all of a continent.
     *
     * @param continentId the id of the continent
     * @return the id of the owner, empty if nobody owns all of it
     * @throws IllegalArgumentException if the id of the continent does not exist
     */
    Optional<String> getContinentOwner(String continentId);

    /**
     * Sum of the bonuses of the continents the player owns completely.
     *
     * @param playerId the id of the player
     * @return the extra armies, 0 if they don't own a whole continent
     */
    int getContinentBonus(String playerId);

    /**
     * Tells if you can get from one territory to the other passing only through territories
     * of that player. Used to check the move at the end of the turn.
     *
     * @param fromId starting territory
     * @param toId destination territory
     * @param playerId the player that has to own the whole path
     * @return true if the path exists
     * @throws IllegalArgumentException if one of the two ids does not exist
     */
    boolean areConnected(String fromId, String toId, String playerId);
}
