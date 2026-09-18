package it.unibo.risiko.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardTerritories;
import it.unibo.risiko.model.deck.CardTroops;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.ai.AggressiveStrategy;
import it.unibo.risiko.model.player.strategy.ai.DefensiveStrategy;
import it.unibo.risiko.model.player.strategy.ai.RandomStrategy;

/**
 * Test class for player general player and "human interactions".
 */
final class PlayerTest {
    private static final String HUMAN_NAME = "human";
    private static final String AGGRESSIVE_NAME = "aggressive";
    private static final String DEFENSIVE_NAME = "defensive";
    private static final String RANDOM_NAME = DEFENSIVE_NAME; //equal on purpose
    private static final String ALASKA_ID = "alaska"; 
    private static final String INDONESIA_ID = "indonesia";
    private static final String MONGOLIA_ID = "mongolia";

    private GameMap map;
    private Roster roster;

    @BeforeEach void init() throws IOException {
        this.map = MapLoader.loadDefault();
        this.roster = new RosterImpl(List.of(
            new PlayerRequest(HUMAN_NAME, PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest(AGGRESSIVE_NAME, PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest(DEFENSIVE_NAME, PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.GREEN),
            new PlayerRequest(RANDOM_NAME, PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        );
    }

    @Test void rosterCreationTest() {
        //testing if Roster breaks with wrong inputs
        assertThrows(IllegalArgumentException.class, () -> new RosterImpl(List.of(), map));
        assertThrows(NullPointerException.class,
             () -> new RosterImpl(List.of(
                new PlayerRequest(
                    DEFENSIVE_NAME, 
                    PlayerRequest.PlayerStrategyRequest.RANDOM, 
                    RisikoColors.RED
                ), null, null), map)
            );
        assertThrows(IllegalArgumentException.class, () -> new RosterImpl(List.of(
            new PlayerRequest(HUMAN_NAME, PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest(AGGRESSIVE_NAME, PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest(DEFENSIVE_NAME, PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.BLACK),
            new PlayerRequest(RANDOM_NAME, PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        ));
    }

    @Test void rosterCorrectyCreated() {
        //generates the correct number of players?
        assertEquals(4, roster.getAllPlayers().size());
        //testing if name assignement is correct
        assertEquals(HUMAN_NAME, roster.getPlayer(RisikoColors.BLACK).get().getName());
        assertEquals(AGGRESSIVE_NAME, roster.getPlayer(RisikoColors.YELLOW).get().getName());
        assertNotEquals(DEFENSIVE_NAME, roster.getPlayer(RisikoColors.BLACK).get().getName());
        //all players have different ids
        assertEquals(roster.getAllPlayers().size(), roster.getAllPlayers().stream().map(Player::getId).distinct().count());
        //testing if is human works correctly
        assertTrue(roster.getPlayer(RisikoColors.BLACK).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.YELLOW).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.GREEN).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.RED).get().isHuman());
        //testing if searching for a non existent player works
        assertEquals(roster.getPlayer(RisikoColors.PINK), Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> roster.getPlayer("null"));
        //testing is strategy assignmets is in line with requests
        assertTrue(roster.getPlayer(RisikoColors.BLACK).get().getStrategy() instanceof HumanStrategy);
        assertTrue(roster.getPlayer(RisikoColors.YELLOW).get().getStrategy() instanceof AggressiveStrategy);
        assertTrue(roster.getPlayer(RisikoColors.GREEN).get().getStrategy() instanceof DefensiveStrategy);
        assertTrue(roster.getPlayer(RisikoColors.RED).get().getStrategy() instanceof RandomStrategy);
    }

    //we dont care if the event follows the rules, just that the information it recives are correctly built
    @Test void correctHumanAttack() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory(ALASKA_ID).setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        //attack test
        interactive.attackDestination(this.map.getTerritory(ALASKA_ID));
        assertFalse(interactive.canCreateAttack());
        interactive.attackSource(this.map.getTerritory(INDONESIA_ID));
        assertFalse(interactive.canCreateAttack());
        assertThrows(IllegalStateException.class, () -> interactive.getAttack(human));
        interactive.attackStrenght(3);
        // ugly but this should be inside player so it can ask for its owner
        assertTrue(interactive.canCreateAttack());
        final var output = interactive.getAttack(human).get();
        assertEquals(INDONESIA_ID, output.attackSource().getId());
        assertEquals(ALASKA_ID, output.attackDestination().getId());
        assertEquals(3, output.attackerStrength());
        assertEquals(3, output.defenderStrength());
        assertEquals(HUMAN_NAME, output.attacker().getName());
        assertEquals(AGGRESSIVE_NAME, output.defender().getName());
        assertFalse(interactive.canCreateAttack());
    }

    //reinforce test
    @Test void correctHumanReinforce() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory(ALASKA_ID).setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        assertFalse(interactive.canCreateReinforce());
        interactive.reinforce(Map.of(this.map.getTerritory(INDONESIA_ID), 3));
        assertTrue(interactive.canCreateReinforce());
        assertThrows(IllegalStateException.class, () -> interactive.getReinforce(human, 4));
        final var reinforce = interactive.getReinforce(human, 3);
        assertEquals(human, reinforce.player());
        assertEquals(Map.of(this.map.getTerritory(INDONESIA_ID), 3), reinforce.reinforcement());
        assertFalse(interactive.canCreateReinforce());
    }


    //move test
    @Test void correctHumanMove() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory(ALASKA_ID).setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        interactive.moveDestination(this.map.getTerritory(INDONESIA_ID));
        assertFalse(interactive.canCreateMove());
        interactive.moveSource(this.map.getTerritory(MONGOLIA_ID));
        assertThrows(IllegalStateException.class, () -> interactive.getMove(human));
        interactive.moveStrenght(2);
        assertTrue(interactive.canCreateMove());
        var move = interactive.getMove(human).get();
        assertEquals(human, move.player());
        assertEquals(MONGOLIA_ID, move.sourceTerritory().getId());
        assertEquals(this.map.getTerritory(INDONESIA_ID), move.destinationTerritory());
        assertEquals(2, move.troopsMoved());
        move = interactive.getMoveAfterConquest(MONGOLIA_ID, INDONESIA_ID, human);
        assertEquals(human, move.player());
        assertEquals(MONGOLIA_ID, move.sourceTerritory().getId());
        assertEquals(this.map.getTerritory(INDONESIA_ID), move.destinationTerritory());
        assertEquals(1, move.troopsMoved());
    }

    @Test void incorrectHumanMove() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();

        assertThrows(IllegalArgumentException.class, () -> interactive.attackStrenght(0));
        assertThrows(IllegalArgumentException.class, () -> interactive.moveStrenght(0));
        assertThrows(IllegalArgumentException.class, () -> interactive.reinforce(Map.of(this.map.getTerritory(INDONESIA_ID), 0)));
        assertThrows(IllegalArgumentException.class, () -> interactive.attackStrenght(-1));
        assertThrows(IllegalArgumentException.class, () -> interactive.moveStrenght(-1));
        assertThrows(IllegalArgumentException.class, () -> interactive.reinforce(Map.of(this.map.getTerritory(INDONESIA_ID), 0)));
        assertThrows(NullPointerException.class, () -> interactive.reinforce(Map.of(null, 0)));
        assertThrows(NullPointerException.class, () -> interactive.reinforce(Map.of(null, 2)));
    }

    @Test void cardTest() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        assertThrows(IllegalArgumentException.class, () -> interactive.cardsToPlay(List.of(
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY)
        )));
        interactive.cardsToPlay(List.of(
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY)
        ));
        assertTrue(interactive.canPlayCards());
        assertEquals(6, interactive.playCards(null, human).get().gainedArmies()); //null is leftovers of ai methods doesn't use hand
        interactive.cardsToPlay(List.of(
            new Card(CardTerritories.NEWGUINEA, CardTroops.CANNONS),
            new Card(CardTerritories.NEWGUINEA, CardTroops.CAVALRY),
            new Card(CardTerritories.NEWGUINEA, CardTroops.INFANTRY)
        ));
        assertEquals(10, interactive.playCards(null, human).get().gainedArmies()); //null is leftovers of ai methods doesn't use hand
    }

    @Test void flushTest() {
        final Player human = roster.getPlayer(RisikoColors.BLACK).get();
        final HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory(ALASKA_ID).setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        interactive.moveDestination(this.map.getTerritory(INDONESIA_ID));
        interactive.moveSource(this.map.getTerritory(MONGOLIA_ID));
        interactive.moveStrenght(2);
        interactive.reinforce(Map.of(this.map.getTerritory(INDONESIA_ID), 3)); 
        interactive.attackDestination(this.map.getTerritory(ALASKA_ID));
        interactive.attackSource(this.map.getTerritory(INDONESIA_ID));
        interactive.attackStrenght(3);
        assertTrue(interactive.canCreateAttack());
        assertTrue(interactive.canCreateMove());
        assertTrue(interactive.canCreateReinforce());
        interactive.flush();
        assertFalse(interactive.canCreateAttack());
        assertFalse(interactive.canCreateMove());
        assertFalse(interactive.canCreateReinforce());
    }
}
