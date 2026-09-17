package it.unibo.risiko.model.battle;

import java.util.ArrayList;
import java.util.List;

/**
 * Dice with the results decided before, used in the tests instead of {@link RandomDice}.
 * The rolls come out in the order they were added.
 */
final class FixedDice implements Dice {

    private final List<List<Integer>> rolls = new ArrayList<>();
    private int next;

    /**
     * Prepares the next roll.
     *
     * @param values the dice that have to come out, already sorted from the highest
     * @return this dice, to chain the calls
     */
    FixedDice addRoll(final List<Integer> values) {
        this.rolls.add(List.copyOf(values));
        return this;
    }

    @Override
    public List<Integer> roll(final int amount) {
        if (this.next >= this.rolls.size()) {
            throw new IllegalStateException("The test did not prepare other rolls");
        }
        final var values = this.rolls.get(this.next);
        this.next++;
        if (values.size() != amount) {
            throw new IllegalStateException(
                    "Expected " + amount + " dice, the test prepared " + values.size());
        }
        return values;
    }
}
