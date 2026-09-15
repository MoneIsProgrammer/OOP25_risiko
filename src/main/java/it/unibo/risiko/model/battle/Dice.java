package it.unibo.risiko.model.battle;

import java.util.List;

/**
 * Rolling the dice.
 * It's an interface because the tests need dice with results decided in advance, otherwise
 * you can't check how a battle ends. The AI must use its own dice when it simulates an
 * attack: with the game's dice it would change the rolls of the real game.
 */
@FunctionalInterface
public interface Dice {

    /** Most dice you can roll at once. */
    int MAX_DICE = 3;

    /**
     * Rolls the dice.
     *
     * @param amount how many dice to roll, from 1 to {@link #MAX_DICE}
     * @return the values that came out, from the highest to the lowest
     * @throws IllegalArgumentException if amount is not between 1 and {@link #MAX_DICE}
     */
    List<Integer> roll(int amount);
}
