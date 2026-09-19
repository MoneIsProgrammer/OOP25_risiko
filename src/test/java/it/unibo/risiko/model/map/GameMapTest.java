package it.unibo.risiko.model.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the map on a small map made by hand.
 */
class GameMapTest {

    private static final String NORTH = "north";
    private static final String SOUTH = "south";
    private static final String N1 = "n1";
    private static final String N2 = "n2";
    private static final String N3 = "n3";
    private static final String S1 = "s1";
    private static final String P1 = "p1";
    private static final String P2 = "p2";

    private static final String CONTINENT_ID = "c";
    private static final String CONTINENT_NAME = "C";
    private static final String TERRITORY_ID = "t";
    private static final String TERRITORY_NAME = "T";

    private static final int NORTH_BONUS = 3;
    private static final int SOUTH_BONUS = 5;

    private GameMap map;

    // north has n1 - n2 - n3 in a row, south has only s1, next to n3
    @BeforeEach
    void prepareMap() {
        this.map = new GameMapBuilder()
                .addContinent(NORTH, "North", NORTH_BONUS)
                .addContinent(SOUTH, "South", SOUTH_BONUS)
                .addTerritory(N1, "North one", NORTH)
                .addTerritory(N2, "North two", NORTH)
                .addTerritory(N3, "North three", NORTH)
                .addTerritory(S1, "South one", SOUTH)
                .addAdjacency(N1, N2)
                .addAdjacency(N2, N3)
                .addAdjacency(N3, S1)
                .build();
    }

    // i write each border once, it has to work both ways
    @Test
    void bordersWorkBothWays() {
        assertTrue(this.map.areAdjacent(N1, N2));
        assertTrue(this.map.areAdjacent(N2, N1));
        // same row but not next to each other
        assertFalse(this.map.areAdjacent(N1, N3));
    }

    @Test
    void territoriesOfAPlayer() {
        this.map.getTerritory(N1).setOwner(P1);
        this.map.getTerritory(N2).setOwner(P2);
        assertEquals(1, this.map.getTerritoriesOf(P1).size());
        // a player with nothing gets an empty set, not an error
        assertTrue(this.map.getTerritoriesOf("p3").isEmpty());
    }

    // the bonus of a continent counts only when the continent is all yours
    @Test
    void bonusOnlyForWholeContinents() {
        assertEquals(0, this.map.getContinentBonus(P1));

        this.map.getTerritory(N1).setOwner(P1);
        this.map.getTerritory(N2).setOwner(P1);
        this.map.getTerritory(N3).setOwner(P1);
        assertEquals(NORTH_BONUS, this.map.getContinentBonus(P1));

        this.map.getTerritory(S1).setOwner(P1);
        assertEquals(NORTH_BONUS + SOUTH_BONUS, this.map.getContinentBonus(P1));
    }

    @Test
    void builderRefusesWrongMaps() {
        // territory in a continent that doesn't exist
        assertThrows(IllegalArgumentException.class, () -> new GameMapBuilder()
                .addContinent(CONTINENT_ID, CONTINENT_NAME, 1)
                .addTerritory(TERRITORY_ID, TERRITORY_NAME, "no-continent"));
        // same id two times
        assertThrows(IllegalArgumentException.class, () -> new GameMapBuilder()
                .addContinent(CONTINENT_ID, CONTINENT_NAME, 1)
                .addTerritory(TERRITORY_ID, TERRITORY_NAME, CONTINENT_ID)
                .addTerritory(TERRITORY_ID, "Copy", CONTINENT_ID));
        // a territory with no borders can't be reached, so no game
        assertThrows(IllegalStateException.class, () -> new GameMapBuilder()
                .addContinent(CONTINENT_ID, CONTINENT_NAME, 1)
                .addTerritory(TERRITORY_ID, TERRITORY_NAME, CONTINENT_ID)
                .build());
    }
}
