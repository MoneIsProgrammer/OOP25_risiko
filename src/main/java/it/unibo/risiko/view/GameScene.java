package it.unibo.risiko.view;

import java.io.IOException;

import it.unibo.risiko.controller.MapClickHandler;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.view.map.DiceCanvas;
import it.unibo.risiko.view.map.MapCanvas;
import it.unibo.risiko.view.map.MapLayout;
import it.unibo.risiko.view.map.MapView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * GameScene
 */
public class GameScene extends Scene{
    
    //unused private static final double WINDOW_WIDTH = 1100;
    //unused private static final double WINDOW_HEIGHT = 700;

    final MapCanvas canvas;

    public GameScene(Roster roster, GameMap map, History history) { //TODO add necessary paramenters for controller view comunication
        
        final var root = new BorderPane();
        super(root);
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

        box.getChildren().add(new GameLogBox(history));

        // the map follows the size of the window
        final var container = new Pane(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        root.setCenter(container);
        root.setRight(box);

    }
}
