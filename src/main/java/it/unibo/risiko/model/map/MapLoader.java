package it.unibo.risiko.model.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reads the map file and passes what it finds to the {@link GameMapBuilder}.
 * It only reads text, the checks are the builder's job, so a new file format would
 * just need a new loader.
 */
public final class MapLoader {

    /** Path of the default map inside the resources. */
    public static final String DEFAULT_MAP = "/it/unibo/risiko/world.txt";

    /** Field separator in the map file. */
    public static final String SEPARATOR = ";";

    /** Prefix of the comment lines. */
    public static final String COMMENT = "#";

    private static final String LINE_PREFIX = "Line ";
    private static final int CONT_FIELDS = 4;
    private static final int TERR_FIELDS = 4;
    private static final int ADJ_FIELDS = 3;

    private MapLoader() {
    }

    /**
     * Loads the world map from the resources.
     *
     * @return the world map
     * @throws IOException if the file is missing or wrong
     */
    public static GameMap loadDefault() throws IOException {
        try (InputStream input = MapLoader.class.getResourceAsStream(DEFAULT_MAP)) {
            if (input == null) {
                throw new IOException("Map file not found: " + DEFAULT_MAP);
            }
            return load(input);
        }
    }

    /**
     * Reads a map from any stream. The tests use it to load small maps written by hand.
     *
     * @param input the stream to read, in UTF-8
     * @return the map that was built
     * @throws IOException if the file is broken or has wrong references
     */
    public static GameMap load(final InputStream input) throws IOException {
        final GameMapBuilder builder = new GameMapBuilder();
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while (line != null) {
                lineNumber++;
                final String clean = line.trim();
                if (!clean.isEmpty() && !clean.startsWith(COMMENT)) {
                    readLine(clean, lineNumber, builder);
                }
                line = reader.readLine();
            }
        }
        try {
            return builder.build();
        } catch (final IllegalStateException e) {
            // the text was fine but the map itself doesn't make sense
            throw new IOException("Map not valid: " + e.getMessage(), e);
        }
    }

    private static void readLine(final String line, final int lineNumber, final GameMapBuilder builder)
            throws IOException {
        final String[] fields = line.split(SEPARATOR);
        // a line with only ';' splits into an empty array
        check(fields.length > 0, lineNumber, "there is nothing on this line");
        final String type = fields[0].trim();
        try {
            switch (type) {
                case "CONT" -> {
                    check(fields.length >= CONT_FIELDS, lineNumber, "CONT line is incomplete");
                    builder.addContinent(fields[1].trim(), fields[2].trim(), readInt(fields[3], lineNumber));
                }
                case "TERR" -> {
                    check(fields.length >= TERR_FIELDS, lineNumber, "TERR line is incomplete");
                    builder.addTerritory(fields[1].trim(), fields[2].trim(), fields[3].trim());
                }
                case "ADJ" -> {
                    check(fields.length >= ADJ_FIELDS, lineNumber, "ADJ line is incomplete");
                    builder.addAdjacency(fields[1].trim(), fields[2].trim());
                }
                default -> throw new IOException(LINE_PREFIX + lineNumber + ": unknown type '" + type + "'");
            }
        } catch (final IllegalArgumentException e) {
            // the builder found the error, but only here we know the line number
            throw new IOException(LINE_PREFIX + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private static int readInt(final String text, final int lineNumber) throws IOException {
        try {
            return Integer.parseInt(text.trim());
        } catch (final NumberFormatException e) {
            throw new IOException(LINE_PREFIX + lineNumber + ": '" + text.trim() + "' is not a number", e);
        }
    }

    private static void check(final boolean condition, final int lineNumber, final String message)
            throws IOException {
        if (!condition) {
            throw new IOException(LINE_PREFIX + lineNumber + ": " + message);
        }
    }
}
