package it.unibo.risiko.model.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the real world map in the file, it's written by hand so it's easy to make mistakes.
 */
class MapLoaderTest {

    private static final int TERRITORIES = 42;
    private static final int CONTINENTS = 6;

    private GameMap world;

    @BeforeEach
    void loadWorld() throws IOException {
        this.world = MapLoader.loadDefault();
    }

    // the numbers of the real risiko board
    @Test
    void theWorldIsComplete() {
        assertEquals(TERRITORIES, this.world.getTerritories().size());
        assertEquals(CONTINENTS, this.world.getContinents().size());
    }

    // if a is next to b then b is next to a, otherwise there's a mistake in the file
    @Test
    void everyBorderGoesBothWays() {
        for (final Territory territory : this.world.getTerritories()) {
            for (final String neighbour : territory.getAdjacentIds()) {
                assertTrue(this.world.areAdjacent(neighbour, territory.getId()),
                        territory.getId() + " is next to " + neighbour + " but not the opposite");
            }
        }
    }

    // a broken file has to give an error, not half a map
    @Test
    void aBrokenFileGivesAnError() {
        // the continent doesn't exist
        assertThrows(IOException.class, () -> MapLoader.load(text(
                "CONT;c;Continent;3",
                "TERR;t;Land;wrong-continent")));
        // the bonus is not a number
        assertThrows(IOException.class, () -> MapLoader.load(text("CONT;c;Continent;three")));
        // a line that means nothing
        assertThrows(IOException.class, () -> MapLoader.load(text("HELLO;c;Continent;3")));
    }

    private static InputStream text(final String... lines) {
        return new ByteArrayInputStream(String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }
}
