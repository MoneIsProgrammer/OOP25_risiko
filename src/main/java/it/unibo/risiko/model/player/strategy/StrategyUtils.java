package it.unibo.risiko.model.player.strategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.deck.CardBonus;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;

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
    Territory lonely = null;
    for (final String adj : territory.getAdjacentIds()) {
        if (map.getTerritory(adj).getOwnerId().get().equals(territory.getOwnerId().get())) {
            counter++;
            lonely = map.getTerritory(adj);
        }
    }
        if (counter == 1) {
            return secondIsolatedCheck(lonely, map); //ok because if counter = 1 lonely has been assigned
        }
        return counter >= 2;
    }

    private static boolean secondIsolatedCheck(final Territory territory, final GameMap map) {var counter = 0;
    for (final String adj : territory.getAdjacentIds()) {
        if (map.getTerritory(adj).getOwnerId().get().equals(territory.getOwnerId().get())) {
            counter++;
        }
    }
        return counter >= 3;
    }

    public static Optional<CardEvent> genericCardPlay(List<Card> hand, Player owner, GameMap map) {
        Collection<Card> best = null;
        int value = 0;
        CardBonus calc = new CardBonus(map);
        if (hand.size() < 3) {
            return Optional.empty();
        }
        for (int i = 0; i < hand.size(); i++) { //stolen from https://www.cs.cornell.edu/courses/cs1112/2011sp/Notes/egL20/L20post.pdf?
            for (int j = i + 1; j < hand.size(); j++) {
                for (int z = j + 1; z < hand.size(); z++) {
                    var combination = List.of(hand.get(i), hand.get(j), hand.get(z));
                    var temp = calc.calculateThreeBonus(combination, owner.getId());
                    if (value < temp) {
                        value = temp;
                        best = combination;
                    }
                }
            }
        }
        if (best == null) {
            return Optional.empty();
        }
        return Optional.of(new CardEvent(best,owner,value));
    
    }
}
