package it.unibo.risiko.controller;

import java.io.IOException;
import java.util.List;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.view.map.DiceCanvas;
import it.unibo.risiko.view.map.MapCanvas;
import it.unibo.risiko.view.map.MapLayout;
import it.unibo.risiko.view.map.MapView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * GameController
 */
public class GameController {

    private static final double WINDOW_WIDTH = 1100;
    private static final double WINDOW_HEIGHT = 700;

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
        final MapCanvas canvas;
        try {
            canvas = new MapCanvas(map, MapLayout.loadDefault());
        } catch (final IOException e) {
            throw new IllegalStateException("Could not load the map layout", e);
        }

        // from here we use it only as a MapView
        final MapView mapView = canvas;
        for (final Player player : roster.getAllPlayers()) {
            mapView.setPlayerColor(player.getId(), player.getColor());
        }

        // clicks for the attack and the move
        final var clickHandler = new MapClickHandler(map, mapView);
        mapView.addTerritoryClickListener(clickHandler);
        clickHandler.addChoiceListener((from, to) -> {
            // TODO put here the HumanStrategy calls, from and to are the ids of the territories
        });
        // TODO call clickHandler.setTurn when the turn or the phase changes

        var box = new VBox();
        for (Player player : roster.getAllPlayers()) {
            box.getChildren().add(new Text(player.getName() + player.getId() + player.getColor().name()));
        }

        // the dice of the last attack, under the players
        final var dice = new DiceCanvas();
        box.getChildren().add(dice);
        // TODO register mapView to the game events, redraw it after dealing the territories
        // TODO give the result of every AttackResultEvent to dice.setResult

        // the map follows the size of the window
        final var container = new Pane(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());

        final var root = new BorderPane();
        root.setCenter(container);
        root.setRight(box);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
    }

}
