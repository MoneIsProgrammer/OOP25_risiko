package it.unibo.risiko.model.map;

import it.unibo.risiko.model.common.Identifiable;
import java.util.Optional;
import java.util.Set;

/**
 * A territory of the map.
 * Name, continent and neighbours are fixed when the map is built, only the owner and
 * the armies change during the game. The owner is just the player id, so the map
 * package doesn't need the player package.
 */
public interface Territory extends Identifiable {

    /**
     * Name to show on screen.
     *
     * @return the name of the territory
     */
    String getName();

    /**
     * The continent this territory is part of.
     *
     * @return the id of the continent
     */
    String getContinentId();

    /**
     * The neighbouring territories.
     *
     * @return their ids in an unmodifiable set
     */
    Set<String> getAdjacentIds();

    /**
     * Who owns the territory now.
     *
     * @return the id of the owner, empty if nobody owns it yet
     */
    Optional<String> getOwnerId();

    /**
     * Gives the territory to a player.
     *
     * @param playerId the id of the new owner
     */
    void setOwner(String playerId);

    /**
     * The armies on the territory.
     *
     * @return the number of armies
     */
    int getArmies();

    /**
     * Adds armies to the territory.
     *
     * @param amount how many armies to add, not negative
     * @throws IllegalArgumentException if amount is negative
     */
    void addArmies(int amount);

    /**
     * Removes armies from the territory.
     *
     * @param amount how many armies to remove, not negative
     * @throws IllegalArgumentException if amount is negative or bigger than the armies present
     */
    void removeArmies(int amount);
}
