package it.unibo.risiko.model.player;

import it.unibo.risiko.model.map.GameMap;

/**
 * The factory that is used to create players with different strategies.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface PlayerFactory {
    /**
     * Creates a player following the passed specifications.
     * 
     * @param roster so the player knows the other players
     * @param map the map the player will play on
     * @param request necessary informations to build the player
     * @param nOfPlayers how many players are in the games, sets the starting troops
     * 
     * @return the player built following specifications
     */
    Player generatePlayer(PlayerRequest request, Roster roster, GameMap map, int nOfPlayers);
}
