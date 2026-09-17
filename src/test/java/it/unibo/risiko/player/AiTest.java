package it.unibo.risiko.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerFactoryImpl;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;

/**
 * Test the 3 types of ai.
 */
final class AiTest {

    private static final String A_ID = "a";
    private static final String B_ID = "b";
    private static final String C_ID = "c";
    private static final String D_ID = "d";
    private static final String E_ID = "e";
    private static final String F_ID = "f";
    private GameMap map;
    private Player human; //dummy not tested
    private Player aggressive;
    private Player defensive;
    private Player random;

    @BeforeEach void init() throws IOException {
        this.map = MapLoader.load(
            MapLoader.class.getResourceAsStream("/it/unibo/risiko/AItestMap.txt")
        ); //copied from implemented class
        //       a - b - c     3 - 10 - 2
        //       |   | /       |    | /
        //       d - e - f     4 - 9 - 6
        final Roster roster = new RosterImpl(List.of(
            new PlayerRequest("human", PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest("aggressive", PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest("defensive", PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.GREEN),
            new PlayerRequest("random", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        );
        final PlayerFactoryImpl factory = new PlayerFactoryImpl();
        human = roster.getPlayer(RisikoColors.BLACK).get();
        aggressive = roster.getPlayer(RisikoColors.YELLOW).get();
        defensive = roster.getPlayer(RisikoColors.GREEN).get();
        random = factory.generateSeededRandom(
            new PlayerRequest("random", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED),
            roster, map, 4);
        this.map.getTerritories().forEach(a -> a.setOwner(human.getId()));
        final int aStarting = 3;
        this.map.getTerritory(A_ID).addArmies(aStarting);
        final int bStarting = 10;
        this.map.getTerritory(B_ID).addArmies(bStarting);
        final int cStarting = 2;
        this.map.getTerritory(C_ID).addArmies(cStarting);
        final int dStarting = 4;
        this.map.getTerritory(D_ID).addArmies(dStarting);
        final int eStarting = 9;
        this.map.getTerritory(E_ID).addArmies(eStarting);
        final int fStarting = 6;
        this.map.getTerritory(F_ID).addArmies(fStarting);

    }

    @Test void aggressiveAiTest() {
        final int expectedMove = 9;

        this.map.getTerritory(E_ID).setOwner(aggressive.getId());
        var attack = this.aggressive.attack();
        assertEquals(E_ID, attack.get().attackSource().getId());
        assertEquals(C_ID, attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(2, attack.get().defenderStrength());
        //
        this.map.getTerritory(B_ID).setOwner(aggressive.getId());
        attack = this.aggressive.attack();
        assertEquals(B_ID, attack.get().attackSource().getId());
        assertEquals(C_ID, attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(2, attack.get().defenderStrength());
        //
        this.map.getTerritory(C_ID).setOwner(aggressive.getId());
        attack = this.aggressive.attack();
        assertEquals(B_ID, attack.get().attackSource().getId());
        assertEquals(A_ID, attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(3, attack.get().defenderStrength());

        final var moveAc = aggressive.getMoveAfterConquest(B_ID, C_ID);
        assertEquals(B_ID, moveAc.sourceTerritory().getId());
        assertEquals(C_ID, moveAc.destinationTerritory().getId());
        assertEquals(expectedMove, moveAc.troopsMoved());
        var move = this.aggressive.move();
        assertEquals(Optional.empty(), move);

        this.map.getTerritory(C_ID).addArmies(expectedMove);
        this.map.getTerritory(B_ID).removeArmies(expectedMove);
        move = this.aggressive.move();
        assertEquals(C_ID, move.get().sourceTerritory().getId());
        assertEquals(B_ID, move.get().destinationTerritory().getId());
        assertEquals(expectedMove, move.get().troopsMoved());

        this.map.getTerritories().forEach(a -> a.removeArmies(a.getArmies() - 1)); // sets all territories to 1 army
        this.map.getTerritory(F_ID).setOwner(aggressive.getId());
        final var reinforce = aggressive.reinforce(100);
        final int remainderArmies = 49;
        assertEquals(Map.of(
        this.map.getTerritory(F_ID), 1, 
        this.map.getTerritory(C_ID), 1, 
        this.map.getTerritory(E_ID), remainderArmies, 
        this.map.getTerritory(B_ID), remainderArmies
        ), 
        reinforce.reinforcement());
    }

    @Test void defensiveAiTest() {
        final int expectedMove = 7;
        final int moveAmount = 9;
        //attacks only when stronger
        this.map.getTerritory(D_ID).setOwner(defensive.getId());
        this.map.getTerritory(D_ID).removeArmies(1);
        var attack = this.defensive.attack();
        assertEquals(Optional.empty(), attack);

        this.map.getTerritory(E_ID).setOwner(defensive.getId());
        attack = this.defensive.attack();
        assertEquals(E_ID, attack.get().attackSource().getId());
        assertEquals(C_ID, attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(2, attack.get().defenderStrength());

        this.map.getTerritory(C_ID).setOwner(defensive.getId());
        this.map.getTerritory(B_ID).setOwner(defensive.getId());

        //check if moves only half of troops after isolated conquest
        final var moveAc = defensive.getMoveAfterConquest(B_ID, C_ID);
        assertEquals(B_ID, moveAc.sourceTerritory().getId());
        assertEquals(C_ID, moveAc.destinationTerritory().getId());
        assertEquals(4, moveAc.troopsMoved());

        // move only from nonborder to weakestborder
        var move = this.defensive.move();
        assertEquals(Optional.empty(), move);
        this.map.getTerritory(F_ID).setOwner(this.defensive.getId());
        move = this.defensive.move();
        assertEquals(D_ID, move.get().destinationTerritory().getId());
        assertEquals(E_ID, move.get().sourceTerritory().getId());
        assertEquals(expectedMove, move.get().troopsMoved());

        this.map.getTerritory(C_ID).addArmies(moveAmount);
        this.map.getTerritory(B_ID).removeArmies(moveAmount);
        move = this.defensive.move();
        assertEquals(C_ID, move.get().sourceTerritory().getId());
        assertEquals(B_ID, move.get().destinationTerritory().getId());
        assertEquals(moveAmount, move.get().troopsMoved());

        this.map.getTerritories().forEach(a -> a.removeArmies(a.getArmies() - 1)); // sets all territories to 1 army
        final var reinforce = defensive.reinforce(100);
        final int remainderArmies = 47;
        assertEquals(Map.of(
        this.map.getTerritory(F_ID), 2,
        this.map.getTerritory(C_ID), 2,
        this.map.getTerritory(E_ID), 2,
        this.map.getTerritory(D_ID), remainderArmies,
        this.map.getTerritory(B_ID), remainderArmies
        ), 
        reinforce.reinforcement());
    }

    @Test void randomTest() {
        this.map.getTerritory(D_ID).setOwner(random.getId());
        var attack = this.random.attack();
        assertEquals(Optional.empty(), attack);
        attack = this.random.attack();
        assertEquals(Optional.empty(), attack);
        attack = this.random.attack();
        assertEquals(Optional.empty(), attack);
        attack = this.random.attack();
        assertEquals(Optional.empty(), attack);
        attack = this.random.attack();
        assertNotEquals(Optional.empty(), attack);
        assertEquals(2, attack.get().attackerStrength()); //doesnt attack with full force
        assertEquals(3, attack.get().defenderStrength());
        assertEquals(D_ID, attack.get().attackSource().getId());
        assertEquals(A_ID, attack.get().attackDestination().getId());
        attack = this.random.attack();
        assertNotEquals(Optional.empty(), attack); //kinda random
        attack = this.random.attack();
        assertEquals(Optional.empty(), attack);

        this.map.getTerritory(A_ID).setOwner(random.getId());
        this.map.getTerritory(E_ID).setOwner(random.getId());
        this.map.getTerritory(D_ID).addArmies(10);

        var moveAc = this.random.getMoveAfterConquest(D_ID, A_ID);
        assertEquals(A_ID, moveAc.destinationTerritory().getId());
        assertEquals(D_ID, moveAc.sourceTerritory().getId());
        assertEquals(3, moveAc.troopsMoved());
        moveAc = this.random.getMoveAfterConquest(D_ID, A_ID);
        assertEquals(A_ID, moveAc.destinationTerritory().getId());
        assertEquals(D_ID, moveAc.sourceTerritory().getId());
        assertNotEquals(3, moveAc.troopsMoved());

        var reinforce = this.random.reinforce(100);
        final var reinforce2 = this.random.reinforce(100);
        assertNotEquals(reinforce2.reinforcement(), reinforce.reinforcement());
        reinforce = this.random.reinforce(100);
        assertNotEquals(reinforce2.reinforcement(), reinforce.reinforcement());

        final var move = this.random.move();
        final int expected = 6;
        assertEquals(expected, move.get().troopsMoved());
        assertEquals(A_ID, move.get().destinationTerritory().getId());
        assertEquals(D_ID, move.get().sourceTerritory().getId());
        final var move2 = this.random.move();
        assertNotEquals(move, move2);
    }
}
