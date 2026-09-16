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

    public GameController(List<PlayerRequest> a) {
        try {
            map = MapLoader.loadDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.roster = new RosterImpl(a, map);//Build map before players then the territories must be assigned
        this.turn = new PlayerTurn(roster);
        this.phase = Phase.SETUP;
    }

    public void start(Stage stage) {
        this.gameGui = new GameScene(roster,map,history, pairSelected(), null, getStrenght());
        stage.setScene(gameGui);
    }

    private Consumer<Integer> getStrenght() {
        // TODO Auto-generated method stub
        return new Consumer<Integer>() {

            @Override
            public void accept(Integer t) {
                System.out.println(t);
            }

        };
    }

    private Consumer<Map<String, Integer>> getReiforceMap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReiforceMap'");
    }

    private BiConsumer<String, String> pairSelected() {
        return new BiConsumer<String,String>() {

            @Override
            public void accept(String from, String to) {
                sourceId = from;
                destinationId = to;
            }
            
        };
    }

}
