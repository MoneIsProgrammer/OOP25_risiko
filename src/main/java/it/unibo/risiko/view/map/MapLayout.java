package it.unibo.risiko.view.map;

import it.unibo.risiko.model.map.MapLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Where each territory goes on screen, and the color of each continent.
 * Only the drawing needs these, so they stay in the view: the model has no pixels and can be
 * tested without opening a window. It reads the same file as {@code MapLoader}, but only looks
 * at coordinates and colors.
 */
public final class MapLayout {

    /** Width of the grid the coordinates in the file are written on. */
    public static final double LOGIC_WIDTH = 1000;

    /** Height of the grid the coordinates in the file are written on. */
    public static final double LOGIC_HEIGHT = 600;

    private static final Color FALLBACK_COLOR = Color.rgb(180, 180, 180);
    private static final String LINE_PREFIX = "Line ";
    private static final String TERRITORY_TYPE = "TERR";
    private static final String CONTINENT_TYPE = "CONT";
    private static final int TERR_FIELDS = 6;
    private static final int CONT_FIELDS = 5;
    private static final int ID_COLUMN = 1;
    private static final int X_COLUMN = 4;
    private static final int Y_COLUMN = 5;
    private static final int COLOR_COLUMN = 4;

    private final Map<String, Point2D> positions;
    private final Map<String, Color> continentColors;

    private MapLayout(final Map<String, Point2D> positions, final Map<String, Color> continentColors) {
        this.positions = Map.copyOf(positions);
        this.continentColors = Map.copyOf(continentColors);
    }

    /**
     * Loads the coordinates of the world map from the resources.
     *
     * @return the layout of the world map
     * @throws IOException if the file is missing or badly written
     */
    public static MapLayout loadDefault() throws IOException {
        try (InputStream input = MapLayout.class.getResourceAsStream(MapLoader.DEFAULT_MAP)) {
            if (input == null) {
                throw new IOException("Map file not found: " + MapLoader.DEFAULT_MAP);
            }
            return load(input);
        }
    }

    /**
     * Reads the coordinates from any stream.
     *
     * @param input the stream to read, in UTF-8
     * @return the layout that was read
     * @throws IOException if a territory line has no coordinates or a number is written badly
     */
    public static MapLayout load(final InputStream input) throws IOException {
        final Map<String, Point2D> positions = new HashMap<>();
        final Map<String, Color> colors = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            int lineNumber = 0;
            while (line != null) {
                lineNumber++;
                final String clean = line.trim();
                if (!clean.isEmpty() && !clean.startsWith(MapLoader.COMMENT)) {
                    readLine(clean, lineNumber, positions, colors);
                }
                line = reader.readLine();
            }
        }
        return new MapLayout(positions, colors);
    }

    /**
     * Where to draw a territory, in grid coordinates.
     *
     * @param territoryId id of the territory
     * @return the point on the grid
     * @throws IllegalArgumentException if there is no position for that territory
     */
    public Point2D getPosition(final String territoryId) {
        final Point2D point = this.positions.get(territoryId);
        if (point == null) {
            throw new IllegalArgumentException("No position for " + territoryId);
        }
        return point;
    }

    /**
     * Tells if we know where to draw a territory.
     *
     * @param territoryId id of the territory
     * @return true if the territory can be drawn
     */
    public boolean hasPosition(final String territoryId) {
        return this.positions.containsKey(territoryId);
    }

    /**
     * Color of a continent's box.
     *
     * @param continentId id of the continent
     * @return the color read from the file, or a grey if it is missing
     */
    public Color getContinentColor(final String continentId) {
        return this.continentColors.getOrDefault(continentId, FALLBACK_COLOR);
    }

    private static void readLine(final String line, final int lineNumber,
                                 final Map<String, Point2D> positions,
                                 final Map<String, Color> colors) throws IOException {
        final String[] fields = line.split(MapLoader.SEPARATOR);
        if (fields.length == 0) {
            return;
        }
        final String type = fields[0].trim();
        if (TERRITORY_TYPE.equals(type)) {
            // without coordinates a territory can't be drawn or clicked:
            // better to fail now than to have an invisible territory mid-game
            if (fields.length < TERR_FIELDS) {
                throw new IOException(LINE_PREFIX + lineNumber + ": the territory "
                        + idInLine(fields) + " has no coordinates");
            }
            positions.put(fields[ID_COLUMN].trim(), new Point2D(
                    readNumber(fields[X_COLUMN], lineNumber),
                    readNumber(fields[Y_COLUMN], lineNumber)));
        } else if (CONTINENT_TYPE.equals(type) && fields.length >= CONT_FIELDS) {
            // a missing color is fine instead, the continent just becomes grey
            colors.put(fields[ID_COLUMN].trim(), readColor(fields[COLOR_COLUMN], lineNumber));
        }
    }

    private static String idInLine(final String[] fields) {
        return fields.length > ID_COLUMN ? fields[ID_COLUMN].trim() : "(without id)";
    }

    private static Color readColor(final String text, final int lineNumber) throws IOException {
        try {
            return Color.web(text.trim());
        } catch (final IllegalArgumentException e) {
            throw new IOException(LINE_PREFIX + lineNumber + ": '" + text.trim() + "' is not a color", e);
        }
    }

    private static double readNumber(final String text, final int lineNumber) throws IOException {
        try {
            return Double.parseDouble(text.trim());
        } catch (final NumberFormatException e) {
            throw new IOException(LINE_PREFIX + lineNumber + ": '" + text.trim() + "' is not a number", e);
        }
    }
}
