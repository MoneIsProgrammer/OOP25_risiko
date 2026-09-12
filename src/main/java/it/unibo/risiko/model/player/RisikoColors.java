package it.unibo.risiko.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enum containing the colors avabile to the players.
 */
public enum RisikoColors {
    /**
     * Yellow.
     */
    YELLOW,
    /**
     * Red.
     */
    RED,
    /**
     * Green.
     */
    GREEN,
    /**
     * Blue.
     */
    BLUE,
    /**
     * Pink.
     */
    PINK,
    /**
     * Black.
     */
    BLACK;

    /**
     * Used to get the colors in a different ordered list.
     * 
     * @return a list containing all RisikoColor values shuffled
     */
    public static List<RisikoColors> shuffledValues() {
        final List<RisikoColors> out = new ArrayList<>(List.of(values()));
        Collections.shuffle(out);
        return out;
    }
}
