package it.unibo.risiko.controller;

import java.io.IOException;
import java.util.List;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * GameController
 */
public class GameController {

    Roster roster;
    GameMap map;

    public GameController(List<PlayerRequest> a) {
        try {
            map = MapLoader.loadDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
        roster = new RosterImpl(a, map);//TODO Build map before players then the territories must be assigned
    }

    public void start(Stage stage) {
        // TODO Auto-generated method stub, start the view here by putting the scene in mainstage
        var box = new VBox();
        for (Player player : roster.getAllPlayers()) {
            box.getChildren().add(new Text(player.getName() + player.getId() + player.getColor().name()));
        }
        stage.setScene(new Scene(box));
    }

}
