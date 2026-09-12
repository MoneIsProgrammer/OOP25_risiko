package it.unibo.risiko.model.player.strategy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;

/**
 * Utilities for Strategy implementation.
 */
public final class StrategyUtils {
    public static final Comparator<Territory> TERRITORY_COMPARATOR = (a, b) -> Integer.compare(a.getArmies(), b.getArmies());

    private StrategyUtils() {
    }

    /**
     * Creates a set containing player owned territories that border enemies.
     * 
     * @param playerTerritories all territories owned by a player
     * @param map the game map
     * @return a set containing territories that border at least 1 enemy
     */
    public static Set<Territory> getBorderTerritories(final Set<Territory> playerTerritories, final GameMap map) {
    final Set<Territory> borderTerritories = new HashSet<>();
    for (final Territory territory : playerTerritories) {
        for (final String adj : territory.getAdjacentIds()) {
            if (!playerTerritories.contains(map.getTerritory(adj))) {
                borderTerritories.add(territory);
                break;
            }
        }
    }
    return borderTerritories;
   }

    /**
     * Checks if the territory is NOT completely surrouded by enemies.
     * 
     * @param territory the territory to check
     * @param map the game map
     * @return true if has at least 2 allied neighbors
     */
    // not isolated if has 2 neighbor allies, there are some edgecases
    public static boolean notIsolated(final Territory territory, final GameMap map) {
    var counter = 0;
    for (final String adj : territory.getAdjacentIds()) {
        if (map.getTerritory(adj).getOwnerId().get().equals(territory.getOwnerId().get())) {
            counter++;
        }
    }
        return counter >= 2;
    }
}
