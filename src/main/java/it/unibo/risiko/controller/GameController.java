package it.unibo.risiko.controller;

import java.io.IOException;
import java.util.List;

import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.history.HistoryImpl;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.view.GameScene;
import javafx.stage.Stage;

/**
 * GameController
 */
public class GameController {

    Roster roster;
    GameMap map;
    History history = new HistoryImpl();

    public GameController(List<PlayerRequest> a) {
        try {
            map = MapLoader.loadDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
        roster = new RosterImpl(a, map);//TODO Build map before players then the territories must be assigned
    }

    public void start(Stage stage) {
        stage.setScene(new GameScene(roster,map,history));
    }

}
