package it.unibo.risiko.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class PlayerTest {
    GameMap map;
    Roster roster;

    @BeforeEach void init() throws IOException{
        this.map = MapLoader.loadDefault();
        this.roster = new RosterImpl(List.of(
            new PlayerRequest("human", PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest("bot1", PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest("bot2", PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.GREEN),
            new PlayerRequest("bot2", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        );
    }

    @Test void RosterCreationTest() {
        //testing if Roster breaks with wrong inputs
        assertThrows(IllegalArgumentException.class, () -> new RosterImpl(List.of(), map));
        assertThrows(NullPointerException.class, () -> new RosterImpl(List.of(new PlayerRequest("bot2", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED),null,null), map));
        assertThrows(IllegalArgumentException.class, () -> new RosterImpl(List.of(
            new PlayerRequest("human", PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest("bot1", PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest("bot2", PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.BLACK),
            new PlayerRequest("bot2", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        ));
    }
    @Test void rosterCorrectyCreated() {
        //generates the correct number of players?
        assertEquals( 4, roster.getAllPlayers().size());
        //testing if name assignement is correct
        assertEquals("human", roster.getPlayer(RisikoColors.BLACK).get().getName());
        assertEquals("bot1", roster.getPlayer(RisikoColors.YELLOW).get().getName());
        assertNotEquals( "bot2", roster.getPlayer(RisikoColors.BLACK).get().getName());
        //all players have different ids
        assertEquals(roster.getAllPlayers().size(), roster.getAllPlayers().stream().map(Player::getId).distinct().count());
        //testing if is human works correctly
        assertTrue(roster.getPlayer(RisikoColors.BLACK).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.YELLOW).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.GREEN).get().isHuman());
        assertFalse(roster.getPlayer(RisikoColors.RED).get().isHuman());
        //testing if searching for a non existent player works
        assertEquals(roster.getPlayer(RisikoColors.PINK), Optional.empty());
        assertThrows(IllegalArgumentException.class , () -> roster.getPlayer("null"));
        //testing is strategy assignmets is in line with requests
        assertTrue(roster.getPlayer(RisikoColors.BLACK).get().getStrategy() instanceof HumanStrategy);
        assertTrue(roster.getPlayer(RisikoColors.YELLOW).get().getStrategy() instanceof AggressiveStrategy);
        assertTrue(roster.getPlayer(RisikoColors.GREEN).get().getStrategy() instanceof DefensiveStrategy);
        assertTrue(roster.getPlayer(RisikoColors.RED).get().getStrategy() instanceof RandomStrategy);
    }
    //we dont care if the event follows the rules, just that the information it recives are correctly built
    @Test void correctHumanAttack() {
        Player human = roster.getPlayer(RisikoColors.BLACK).get();
        HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory("alaska").setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        //attack test
        interactive.attackDestination(this.map.getTerritory("alaska"));
        assertFalse(interactive.canCreateAttack());
        interactive.attackSource(this.map.getTerritory("indonesia"));
        assertFalse(interactive.canCreateAttack());
        assertThrows(IllegalStateException.class, () -> interactive.getAttack(human));
        interactive.attackStrenght(3);
        // ugly but this should be inside player so it can ask for its owner
        assertTrue(interactive.canCreateAttack());
        var output = interactive.getAttack(human).get();
        assertEquals("indonesia", output.attackSource().getId());
        assertEquals("alaska", output.attackDestination().getId());
        assertEquals(3, output.attackerStrength());
        assertEquals(3, output.defenderStrength());
        assertEquals("human", output.attacker().getName());
        assertEquals("bot1", output.defender().getName());
        assertFalse(interactive.canCreateAttack());
    }
    
        //reinforce test
    @Test void correctHumanReinforce() {
        Player human = roster.getPlayer(RisikoColors.BLACK).get();
        HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory("alaska").setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        assertFalse(interactive.canCreateReinforce());
        interactive.reinforce(Map.of(this.map.getTerritory("indonesia"),3));
        assertTrue(interactive.canCreateReinforce());
        assertThrows(IllegalStateException.class, () -> interactive.getReinforce(human, 4));
        var reinforce = interactive.getReinforce(human, 3);
        assertEquals(human, reinforce.player());
        assertEquals(Map.of(this.map.getTerritory("indonesia"),3), reinforce.reinforcement());
        assertFalse(interactive.canCreateReinforce());
    }


        //move test
    @Test void correctHumanMove() {
        Player human = roster.getPlayer(RisikoColors.BLACK).get();
        HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory("alaska").setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        interactive.moveDestination(this.map.getTerritory("indonesia"));
        assertFalse(interactive.canCreateMove());
        interactive.moveSource(this.map.getTerritory("mongolia"));
        assertThrows(IllegalStateException.class, () -> interactive.getMove(human));
        interactive.moveStrenght(2);
        assertTrue(interactive.canCreateMove());
        var move = interactive.getMove(human).get();
        assertEquals(human, move.player());
        assertEquals("mongolia", move.sourceTerritory().getId());
        assertEquals(this.map.getTerritory("indonesia"), move.destinationTerritory());
        assertEquals(2, move.troopsMoved());
        move = interactive.getMoveAfterConquest("mongolia", "indonesia", human);
        assertEquals(human, move.player());
        assertEquals("mongolia", move.sourceTerritory().getId());
        assertEquals(this.map.getTerritory("indonesia"), move.destinationTerritory());
        assertEquals(1, move.troopsMoved());

    }
        //TODO missing card test


    @Test void incorrectHumanMove() {
        Player human = roster.getPlayer(RisikoColors.BLACK).get();
        HumanStrategy interactive = (HumanStrategy) human.getStrategy();

        assertThrows(IllegalArgumentException.class, () -> interactive.attackStrenght(0));
        assertThrows(IllegalArgumentException.class, () -> interactive.moveStrenght(0));
        assertThrows(IllegalArgumentException.class, () -> interactive.reinforce(Map.of(this.map.getTerritory("indonesia"), 0)));
        assertThrows(IllegalArgumentException.class, () -> interactive.attackStrenght(-1));
        assertThrows(IllegalArgumentException.class, () -> interactive.moveStrenght(-1));
        assertThrows(IllegalArgumentException.class, () -> interactive.reinforce(Map.of(this.map.getTerritory("indonesia"), 0)));
        assertThrows(NullPointerException.class, () -> interactive.reinforce(Map.of(null, 0)));
        assertThrows(NullPointerException.class, () -> interactive.reinforce(Map.of(null, 2)));
    }

    @Test void flushTest() {
        Player human = roster.getPlayer(RisikoColors.BLACK).get();
        HumanStrategy interactive = (HumanStrategy) human.getStrategy();
        //every territory has 3 troops
        this.map.getTerritories().forEach(a -> a.addArmies(3));
        //every territory is owned by human
        this.map.getTerritories().forEach(a -> a.setOwner(roster.getPlayer(RisikoColors.BLACK).get().getId()));
        //bot1 controls alaska
        this.map.getTerritory("alaska").setOwner(roster.getPlayer(RisikoColors.YELLOW).get().getId());

        interactive.moveDestination(this.map.getTerritory("indonesia"));
        interactive.moveSource(this.map.getTerritory("mongolia"));
        interactive.moveStrenght(2);
        interactive.reinforce(Map.of(this.map.getTerritory("indonesia"),3)); 
        interactive.attackDestination(this.map.getTerritory("alaska"));
        interactive.attackSource(this.map.getTerritory("indonesia"));
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
