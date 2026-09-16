package it.unibo.risiko.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.classfile.ClassFile.Option;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;

public class AiTest {
    GameMap map;
    Roster roster;
    Player human; //dummy not tested
    Player aggressive, defensive, random;

    @BeforeEach void init() throws IOException{
        this.map = MapLoader.load(MapLoader.class.getResourceAsStream("/it/unibo/risiko/AItestMap.txt")); //copied from implemented class
        //       a - b - c     3 - 10 - 2
        //       |   | /       |    | /
        //       d - e - f     4 - 9 - 6
        this.roster = new RosterImpl(List.of(
            new PlayerRequest("human", PlayerRequest.PlayerStrategyRequest.HUMAN, RisikoColors.BLACK),
            new PlayerRequest("aggressive", PlayerRequest.PlayerStrategyRequest.AGGRESSIVE, RisikoColors.YELLOW),
            new PlayerRequest("defensive", PlayerRequest.PlayerStrategyRequest.DEFENSIVE, RisikoColors.GREEN),
            new PlayerRequest("random", PlayerRequest.PlayerStrategyRequest.RANDOM, RisikoColors.RED)
            ),
            this.map
        );
        for (Territory a : this.map.getTerritories()) {
            System.out.println(a.getName());
        }
        human = this.roster.getPlayer(RisikoColors.BLACK).get();
        aggressive = this.roster.getPlayer(RisikoColors.YELLOW).get();
        defensive = this.roster.getPlayer(RisikoColors.GREEN).get();
        random = this.roster.getPlayer(RisikoColors.RED).get();
        this.map.getTerritories().forEach(a -> a.setOwner(human.getId()));
        this.map.getTerritory("a").addArmies(3);
        this.map.getTerritory("b").addArmies(10);
        this.map.getTerritory("c").addArmies(2);
        this.map.getTerritory("d").addArmies(4);
        this.map.getTerritory("e").addArmies(9);
        this.map.getTerritory("f").addArmies(6);

    }

    @Test void aggressiveAiTest() {
        this.map.getTerritory("e").setOwner(aggressive.getId());
        var attack = this.aggressive.attack();
        assertEquals("e", attack.get().attackSource().getId());
        assertEquals("c", attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(2, attack.get().defenderStrength());
        
        this.map.getTerritory("b").setOwner(aggressive.getId());
        attack = this.aggressive.attack();
        assertEquals("b", attack.get().attackSource().getId());
        assertEquals("c", attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(2, attack.get().defenderStrength());

        
        this.map.getTerritory("c").setOwner(aggressive.getId());
        attack = this.aggressive.attack();
        assertEquals("b", attack.get().attackSource().getId());
        assertEquals("a", attack.get().attackDestination().getId());
        assertEquals(3, attack.get().attackerStrength());
        assertEquals(3, attack.get().defenderStrength());

        var reinforce = aggressive.getMoveAfterConquest("b", "c");
        assertEquals("b", reinforce.sourceTerritory().getId());
        assertEquals("c", reinforce.destinationTerritory().getId());
        assertEquals(9, reinforce.troopsMoved());
        var move = this.aggressive.move();
        assertEquals(Optional.empty(), move);

        this.map.getTerritory("c").addArmies(9);
        this.map.getTerritory("b").removeArmies(9);
        move = this.aggressive.move();
        assertEquals("c", move.get().sourceTerritory().getId());
        assertEquals("b", move.get().destinationTerritory().getId());
        assertEquals(9, move.get().troopsMoved());
    }
}
