package it.unibo.risiko.model.battle;

import it.unibo.risiko.model.map.Territory;

/**
 * Resolves a battle between two adjacent territories.
 */
public interface CombatSystem {

    /**
     * Rolls the dice and works out the losses. It doesn't touch the territories, the turn
     * manager is the one that applies the result.
     *
     * @param attacker territory that attacks
     * @param defender territory that defends
     * @param attackingArmies armies that attack, from 1 to {@link Dice#MAX_DICE}, at least
     *                        one army has to stay in the territory
     * @param defendingArmies armies that defend, from 1 to {@link Dice#MAX_DICE}, not more
     *                        than the ones in the territory
     * @return the result of the battle
     * @throws IllegalArgumentException if the numbers of armies are wrong or if the two
     *                                  territories aren't neighbours
     */
    BattleResult resolve(Territory attacker, Territory defender, int attackingArmies, int defendingArmies);

    /**
     * Most dice you can roll attacking from this territory.
     *
     * @param attacker the territory the attack starts from
     * @return the maximum number of dice, 0 if you can't attack from there
     */
    int maxAttackDice(Territory attacker);

    /**
     * Most dice you can roll defending this territory.
     *
     * @param defender the territory that defends
     * @return the maximum number of dice
     */
    int maxDefenceDice(Territory defender);
}
