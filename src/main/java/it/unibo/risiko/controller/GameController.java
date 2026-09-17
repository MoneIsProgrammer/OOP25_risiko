package it.unibo.risiko.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.history.HistoryImpl;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.view.GameScene;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * GameController
 */
public class GameController {

    Roster roster;
    GameMap map;
    Scene gameGui;
    History history = new HistoryImpl();
    PlayerTurn turn;
    Phase phase;
    String sourceId;
    String destinationId;
    final BooleanProperty ableToBuild = new SimpleBooleanProperty(true); // activate button to commit the action if it can be generated, both this and the army are used to check may be moved
    final IntegerProperty armyCounter = new SimpleIntegerProperty(0); //to show player how many armies has to place or wants to utilize
    final IntegerProperty maxArmyforAction = new SimpleIntegerProperty(6); // set this to the maximum troops utilizable for the action, limits if action can be launched by controller parameteters



    public GameController(final List<PlayerRequest> a) {
        try {
            map = MapLoader.loadDefault();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.roster = new RosterImpl(a, map);//Build map before players then the territories must be assigned
        this.turn = new PlayerTurn(roster);
        this.phase = Phase.SETUP;
    }

    public void start(final Stage stage) {
        this.gameGui = new GameScene(roster,map,history, pairSelected(), getReiforceMap(), getStrenght(), ableToBuild, armyCounter, maxArmyforAction);
        stage.setScene(gameGui);
    }

    private Consumer<Integer> getStrenght() {
        // TODO Auto-generated method stub
        return new Consumer<>() {
            //with ArmyCounter this may be useless
            @Override
            public void accept(final Integer t) {
                maxArmyforAction.set(10);
                System.out.println(t); //dirty testing, feel free to remove
            }

        };
    }

    private Consumer<Map<String, Integer>> getReiforceMap() {
        return new Consumer<>() {

            @Override
            public void accept(Map<String, Integer> t) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'accept'");
            }
            
        };
    }

    private BiConsumer<String, String> pairSelected() {
        return new BiConsumer<>() {

            @Override
            public void accept(final String from, final String to) {
                sourceId = from;
                destinationId = to;
            }
            
        };
    }

}
