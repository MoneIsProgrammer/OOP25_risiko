package it.unibo.risiko.model.battle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.risiko.model.map.GameMapBuilder;
import it.unibo.risiko.model.map.Territory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the combat with fixed dice, so the result is always the same.
 */
class CombatSystemTest {

    private static final String CONTINENT = "c";
    private static final String ALPHA = "a";
    private static final String BETA = "b";

    private static final int ALPHA_ARMIES = 5;
    private static final int BETA_ARMIES = 3;

    private static final List<Integer> THREE_HIGH = List.of(6, 5, 2);
    private static final List<Integer> TWO_MEDIUM = List.of(5, 4);
    private static final List<Integer> ONE_FOUR = List.of(4);
    private static final List<Integer> TWO_HIGH = List.of(6, 5);
    private static final List<Integer> TWO_LOW = List.of(2, 1);

    private Territory attacker;
    private Territory defender;

    // small map made on purpose: alpha and beta are next to each other, so alpha can attack beta
    @BeforeEach
    void prepareMap() {
        final var map = new GameMapBuilder()
                .addContinent(CONTINENT, "Continent", 2)
                .addTerritory(ALPHA, "Alpha", CONTINENT)
                .addTerritory(BETA, "Beta", CONTINENT)
                .addAdjacency(ALPHA, BETA)
                .build();
        this.attacker = map.getTerritory(ALPHA);
        this.defender = map.getTerritory(BETA);
        this.attacker.setOwner("p1");
        this.attacker.addArmies(ALPHA_ARMIES);
        this.defender.setOwner("p2");
        this.defender.addArmies(BETA_ARMIES);
    }

    // attack with 3 dice against 2, the attack wins both pairs
    @Test
    void theHighestDiceWin() {
        final var dice = new FixedDice()
                .addRoll(THREE_HIGH)
                .addRoll(TWO_MEDIUM);
        final var combat = new CombatSystemImpl(dice);
        final var result = combat.resolve(this.attacker, this.defender, 3, 2);

        // the dice are compared in pairs from the highest: 6>5 and 5>4, both won by the attack
        // the third die (2) has no pair, so it counts for nothing
        assertEquals(0, result.getAttackerLosses());
        assertEquals(2, result.getDefenderLosses());
        assertFalse(result.isConquered());
    }

    // one die each, both a 4
    @Test
    void aTieGoesToTheDefender() {
        final var dice = new FixedDice()
                .addRoll(ONE_FOUR)
                .addRoll(ONE_FOUR);
        final var combat = new CombatSystemImpl(dice);
        final var result = combat.resolve(this.attacker, this.defender, 1, 1);

        // same number on both dice, in a tie the one who loses is the attacker
        assertEquals(1, result.getAttackerLosses());
        assertEquals(0, result.getDefenderLosses());
    }

    // the defender loses all the armies it has left
    @Test
    void theEmptiedTerritoryIsConquered() {
        // beta starts with 3 armies, here i leave it 2 so that it loses them all
        this.defender.removeArmies(1);
        final var dice = new FixedDice()
                .addRoll(TWO_HIGH)
                .addRoll(TWO_LOW);
        final var combat = new CombatSystemImpl(dice);
        final var result = combat.resolve(this.attacker, this.defender, 2, 2);

        assertEquals(2, result.getDefenderLosses());
        // no armies left, so the territory is taken
        assertTrue(result.isConquered());
    }

    // the same battle as the first test, but here i look at the territories
    @Test
    void combatDoesNotModifyTerritories() {
        final var dice = new FixedDice()
                .addRoll(THREE_HIGH)
                .addRoll(TWO_MEDIUM);
        final var combat = new CombatSystemImpl(dice);
        combat.resolve(this.attacker, this.defender, 3, 2);

        // resolve only says what happened, the losses are applied by the turn manager
        // so here the armies must still be the ones of the start, 5 and 3
        assertEquals(ALPHA_ARMIES, this.attacker.getArmies());
        assertEquals(BETA_ARMIES, this.defender.getArmies());
    }

    // wrong numbers of armies, here the dice are never rolled
    @Test
    void invalidArmies() {
        final var combat = new CombatSystemImpl(new RandomDice());
        // three dice at most, and one army always stays at home
        assertThrows(IllegalArgumentException.class,
                () -> combat.resolve(this.attacker, this.defender, 4, 1));
        // the defender has three armies, so it can't roll four dice
        assertThrows(IllegalArgumentException.class,
                () -> combat.resolve(this.attacker, this.defender, 3, 4));
    }
}
