package it.unibo.risiko.model.battle;

import it.unibo.risiko.model.map.Territory;
import java.util.List;

/**
 * Normal Risiko rules: the dice are compared in pairs starting from the highest ones
 * and when they are equal the defender wins.
 */
public final class CombatSystemImpl implements CombatSystem {

    private final Dice dice;

    /**
     * Creates the combat system.
     *
     * @param dice the dice to use, in the tests we pass dice with fixed values
     */
    public CombatSystemImpl(final Dice dice) {
        this.dice = dice;
    }

    @Override
    public BattleResult resolve(final Territory attacker, final Territory defender,
                                final int attackingArmies, final int defendingArmies) {
        checkArmies(attacker, defender, attackingArmies, defendingArmies);

        final List<Integer> attack = this.dice.roll(attackingArmies);
        final List<Integer> defence = this.dice.roll(defendingArmies);

        int attackerLosses = 0;
        int defenderLosses = 0;
        // if one side rolled fewer dice, the extra ones don't count
        final int comparisons = Math.min(attack.size(), defence.size());
        for (int i = 0; i < comparisons; i++) {
            if (attack.get(i) > defence.get(i)) {
                defenderLosses = defenderLosses + 1;
            } else {
                // defender wins ties
                attackerLosses = attackerLosses + 1;
            }
        }

        final boolean conquered = defenderLosses >= defender.getArmies();
        return new BattleResult(attack, defence, attackerLosses, defenderLosses, conquered);
    }

    @Override
    public int maxAttackDice(final Territory attacker) {
        // one army always has to stay in the territory
        return Math.max(0, Math.min(Dice.MAX_DICE, attacker.getArmies() - 1));
    }

    @Override
    public int maxDefenceDice(final Territory defender) {
        return Math.min(Dice.MAX_DICE, defender.getArmies());
    }

    private void checkArmies(final Territory attacker, final Territory defender,
                             final int attackingArmies, final int defendingArmies) {
        if (attackingArmies < 1 || attackingArmies > Dice.MAX_DICE) {
            throw new IllegalArgumentException("You attack with 1, 2 or 3 armies");
        }
        if (defendingArmies < 1 || defendingArmies > Dice.MAX_DICE) {
            throw new IllegalArgumentException("You defend with 1, 2 or 3 armies");
        }
        if (attacker.getArmies() - attackingArmies < 1) {
            throw new IllegalArgumentException("At least one army has to stay in " + attacker.getName());
        }
        if (defendingArmies > defender.getArmies()) {
            throw new IllegalArgumentException("In " + defender.getName() + " there are only "
                    + defender.getArmies() + " armies");
        }
        if (!attacker.getAdjacentIds().contains(defender.getId())) {
            throw new IllegalArgumentException(attacker.getName() + " and " + defender.getName()
                    + " are not adjacent");
        }
    }
}
