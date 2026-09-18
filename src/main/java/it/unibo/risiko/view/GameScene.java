package it.unibo.risiko.view;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unibo.risiko.controller.GameController;
import it.unibo.risiko.controller.MapClickHandler;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.view.map.DiceCanvas;
import it.unibo.risiko.view.map.MapCanvas;
import it.unibo.risiko.view.map.MapLayout;
import it.unibo.risiko.view.map.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Scene to interact with the game.
 */
public class GameScene {
    //unused private static final double WINDOW_WIDTH = 1100;
    //unused private static final double WINDOW_HEIGHT = 700;

    private GameController controller;

    /**
     * Default constructor.
     * 
     * @param roster who is playing
     * @param map the game map
     * @param history to track past events
     * @param getTerritories consumer to get basics for events
     * @param getReinforcements consumer to get the combination for a reinforce event
     * @param getStrenght consumer to get the number of troops player wants to use
     * @param canGenerate if the player can request an action without running into an error
     * @param armyCounter how many armies player has or can use during an action
     * @param maxArmyforAction the max of troops that can be used during an action,
     *      or must remain after an action(specifically for reinforce event)
     */
    public void start(final Roster roster,
        final GameMap map,
        final History history,
        final Stage stage
    ) { //TODO add necessary paramenters for controller view comunication
        final MapCanvas canvas;
        final BorderPane root;

        root = new BorderPane();
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
            controller.getTerritories(from, to);
            // TODO put here the HumanStrategy calls, from and to are the ids of the territories
        });
        // TODO call clickHandler.setTurn when the turn or the phase changes

        final var box = new VBox();
        for (final Player player : roster.getAllPlayers()) {
            box.getChildren().add(new Text(player.getName() + player.getId() + player.getColor().name()));
        }

        // the dice of the last attack, under the players
        final var dice = new DiceCanvas();
        box.getChildren().add(dice);
        // TODO register mapView to the game events, redraw it after dealing the territories
        // TODO give the result of every AttackResultEvent to dice.setResult
        final var spacing = 5;
        final var bottom = new ChangingBox(controller.getStrenght, controller.armyCounter, controller.ableToBuild, controller.maxArmyforAction, a -> controller.addListener(a) , () -> controller.advancePhase());
        bottom.setAlignment(Pos.CENTER);
        bottom.setSpacing(spacing);
        box.getChildren().addAll(bottom, new GameLogBox(history));

        // the map follows the size of the window
        final var container = new Pane(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        root.setCenter(container);
        root.setRight(box);
        stage.setScene(new Scene(root));

    }

    public GameScene(GameController controller) {
        super();
        this.controller = controller;
    }

}
