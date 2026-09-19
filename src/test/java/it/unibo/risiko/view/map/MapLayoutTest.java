package it.unibo.risiko.view.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.map.Territory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

/**
 * Tests the reading of the coordinates and of the colors of the map.
 */
class MapLayoutTest {

    private static final String EUROPE_LINE = "CONT;europe;Europe;5;#7FA8D9";
    private static final String ITALY_LINE = "TERR;italy;Italy;europe;100;200";
    private static final String EUROPE = "europe";
    private static final String ITALY = "italy";
    private static final String EUROPE_COLOR = "#7FA8D9";
    private static final double ITALY_X = 100;
    private static final double ITALY_Y = 200;

    // a small file with one continent and one territory
    @Test
    void readsCoordinatesAndColors() throws IOException {
        final var layout = MapLayout.load(text("# comment", EUROPE_LINE, ITALY_LINE));

        assertTrue(layout.hasPosition(ITALY));
        assertEquals(new Point2D(ITALY_X, ITALY_Y), layout.getPosition(ITALY));
        assertEquals(Color.web(EUROPE_COLOR), layout.getContinentColor(EUROPE));
    }

    // model and view read the same file, a territory without coordinates
    // would be in the game but not on the screen, so you could never click it
    @Test
    void everyTerritoryKnowsWhereToBeDrawn() throws IOException {
        final var world = MapLoader.loadDefault();
        final var layout = MapLayout.loadDefault();

        for (final Territory territory : world.getTerritories()) {
            assertTrue(layout.hasPosition(territory.getId()),
                    territory.getId() + " has no coordinates, it would not be drawn");
        }
    }

    private static InputStream text(final String... lines) {
        return new ByteArrayInputStream(String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }
}
