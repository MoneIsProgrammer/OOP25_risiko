package it.unibo.risiko.model.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a battle: the dice rolled and the losses of both sides.
 * It only holds data, the turn manager is the one that applies the losses. The dice are
 * kept too, not only the losses, because the dice panel draws them. The lists are copies,
 * so whoever reads the result can't change it.
 */
public final class BattleResult {

    private final List<Integer> attackerRolls;
    private final List<Integer> defenderRolls;
    private final int attackerLosses;
    private final int defenderLosses;
    private final boolean conquered;

    /**
     * Creates the result of a battle.
     *
     * @param attackerRolls the dice of the attacker
     * @param defenderRolls the dice of the defender
     * @param attackerLosses armies lost by the attacker
     * @param defenderLosses armies lost by the defender
     * @param conquered true if the defender was left with no armies
     */
    public BattleResult(final List<Integer> attackerRolls, final List<Integer> defenderRolls,
                        final int attackerLosses, final int defenderLosses, final boolean conquered) {
        this.attackerRolls = new ArrayList<>(attackerRolls);
        this.defenderRolls = new ArrayList<>(defenderRolls);
        this.attackerLosses = attackerLosses;
        this.defenderLosses = defenderLosses;
        this.conquered = conquered;
    }

    /**
     * The dice of the attacker, from the highest to the lowest.
     *
     * @return the values that came out, in an unmodifiable list
     */
    public List<Integer> getAttackerRolls() {
        return Collections.unmodifiableList(this.attackerRolls);
    }

    /**
     * The dice of the defender, from the highest to the lowest.
     *
     * @return the values that came out, in an unmodifiable list
     */
    public List<Integer> getDefenderRolls() {
        return Collections.unmodifiableList(this.defenderRolls);
    }

    /**
     * Armies lost by the attacker.
     *
     * @return the number of armies lost
     */
    public int getAttackerLosses() {
        return this.attackerLosses;
    }

    /**
     * Armies lost by the defender.
     *
     * @return the number of armies lost
     */
    public int getDefenderLosses() {
        return this.defenderLosses;
    }

    /**
     * Tells if the territory was conquered.
     *
     * @return true if the defender was left with no armies
     */
    public boolean isConquered() {
        return this.conquered;
    }

    @Override
    public String toString() {
        return "dice " + this.attackerRolls + " against " + this.defenderRolls
                + " -> attacker -" + this.attackerLosses
                + ", defender -" + this.defenderLosses
                + (this.conquered ? " (territory conquered)" : "");
    }
}
