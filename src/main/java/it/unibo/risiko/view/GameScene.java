package it.unibo.risiko.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import it.unibo.risiko.controller.GameController;
import it.unibo.risiko.controller.MapClickHandler;
import it.unibo.risiko.model.battle.BattleResult;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CreateCardView;
import it.unibo.risiko.model.deck.DrawCard;
import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.turn.AttackPhase;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.utils.ColorConversion;
import it.unibo.risiko.view.map.DiceCanvas;
import it.unibo.risiko.view.map.MapCanvas;
import it.unibo.risiko.view.map.MapLayout;
import it.unibo.risiko.view.map.MapView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Scene to interact with the game.
 */
public class GameScene {
    //unused private static final double WINDOW_WIDTH = 1100;
    //unused private static final double WINDOW_HEIGHT = 700;
    // readable size
    private static final double OBJECTIVE_SIZE = 450;

    private Stage stage;
    private final GameController controller;
    private AttackPhase attackPhase;
    private DrawCard drawCard;
    // kept here so the controller can pass them the events
    private MapView mapView;
    private final DiceCanvas dice = new DiceCanvas();

    /**
     * Default constructor.
     * 
     * @param controller the controller of the game
     */
    public GameScene(final GameController controller) {
        super();
        this.controller = controller;
    }

    /**
     * Staring point.
     * 
     * @param map the game map
     * @param history the history
     * @param mainStage the main stage
     * @param roster the players
     */
    public void start(
        final Roster roster,
        final GameMap map,
        final History history,
        final Stage mainStage
    ) {
        final MapCanvas canvas;
        final BorderPane root;

        root = new BorderPane();
        try {
            canvas = new MapCanvas(map, MapLayout.loadDefault());
        } catch (final IOException e) {
            throw new IllegalStateException("Could not load the map layout", e);
        }
        this.stage = mainStage;

        // from here we use it only as a MapView
        this.mapView = canvas;
        for (final Player player : roster.getAllPlayers()) {
            mapView.setPlayerColor(player.getId(), player.getColor());
        }

        // clicks for the attack, the move and the reinforce
        final var clickHandler = new MapClickHandler(map, mapView);
        mapView.addTerritoryClickListener(clickHandler);
        // the two clicks written
        final var chosen = new Label();
        clickHandler.addChoiceListener((from, to) -> {
            // the controller gives them to the strategy of the player
            controller.territoriesConsumer(from, to);
            chosen.setText(from + " -> " + to);
        });
        // in the reinforce and in the setup a click says where the armies go
        clickHandler.addPlacementListener(territoryId -> {
            controller.placementChosen(territoryId);
        });

        // when the phase changes the clicks change too
        // getNewValue is an Object so i cast it
        controller.addListener(event -> clickHandler.setPhase((Phase) event.getNewValue()));
        // when the turn passes to another player, only a human can click
        controller.addPlayerListener(event -> {
            final var player = (Player) event.getNewValue();
            if (player.isHuman()) {
                clickHandler.setPlayer(player.getId());
            } else {
                // a bot is playing, the clicks do nothing
                clickHandler.ignoreClicks();
            }
        });

        final var box = new VBox();
        final int insetSize = 5;
        for (final Player player : roster.getAllPlayers()) {
            final var text = new Label(player.getName());
            final int fontSize = 20;
            text.setFont(new Font(fontSize));
            text.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(insetSize), null)));
            final var hbox = new HBox(text);
            hbox.setPadding(new Insets(insetSize));
            hbox.setBackground(
                new Background(
                    new BackgroundFill(
                        ColorConversion.toJavaFxColor(player.getColor()),
                        new CornerRadii(insetSize), 
                        null
                    )
                )
            );
            box.getChildren().addAll(hbox, new Separator());

            this.controller.addPlayerListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                final var current = (Player) evt.getNewValue();
                if (current.getId().equals(player.getId())) {
                    text.setText(player.getName() + "'s turn");
                } else {
                    text.setText(player.getName());
                }
            } 
        });
        }

        /* Button that when clicked, shows the objective card of the player */
        final Button objectiveButton = new Button("Show objective");
        /* Gets the objective of the player */
        objectiveButton.setOnAction(e -> {
            // who plays now
            showObjectiveWindow(controller.getCurrentPlayer().getObjective());
        });

        final Button drawButton = new Button("Draw card");

        if (this.controller.getTurn().getCurrentPhase() == Phase.MOVE && attackPhase.canDraw()) {
            drawButton.setOnAction(e -> {
                final Card cardDrew;
                cardDrew = drawCard.drawNewCard();
                showCardDrew(cardDrew);
            });
        }

        // the dice of the last attack, under the players
        box.getChildren().add(dice);
        box.setAlignment(Pos.CENTER);
        final var spacing = insetSize;
        final var bottom = new ChangingBox(
            controller.getGetStrenght(),
            controller.getArmyCounter(), 
            controller.getAbleToBuild(), 
            controller.getMaxArmyforAction(), 
            controller::addListener, 
            controller::advancePhase
        );
        bottom.setAlignment(Pos.CENTER);
        bottom.setSpacing(spacing);
        // no draw button, automatic now
        box.getChildren().addAll(chosen, bottom, new GameLogBox(history), objectiveButton);

        // the map follows the size of the window
        final var container = new Pane(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        root.setCenter(container);
        root.setRight(box);
        mainStage.setScene(new Scene(root));
    }

    /**
     * Gives an event of the game to the map, the controller calls it with the events
     * it gets from the players.
     *
     * @param event what just happened
     */
    public void onGameEvent(final Event event) {
        // before start() there is no map on screen yet
        if (this.mapView == null) {
            return;
        }
        this.mapView.onEvent(event);
    }

    /**
     * Shows the dice of a battle, the controller calls it after resolving an attack.
     *
     * @param result the result of the battle
     */
    public void onBattleResult(final BattleResult result) {
        this.dice.setResult(result);
    }

    /**
     * shows a window that displays the objective card of the player.
     * 
     * @param card the objective of the player
     */
    private void showObjectiveWindow(final Card card) {
        /* Gets the image of the objective card from CardViewImpl */
        final ImageView objectiveView = CreateCardView.createObjectiveView(card, OBJECTIVE_SIZE);
        objectiveView.setPreserveRatio(true);

        /* Context for the objective, shows name of the player whose objective is being shown */
        final Label caption = new Label("Player: " + controller.getCurrentPlayer().getName());
        caption.setWrapText(true);
        caption.setStyle("-fx-font-size: 14px;");

        /* Center the image */
        final VBox root = new VBox(10, objectiveView, caption);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        final Stage objectiveImageStage = new Stage();
        objectiveImageStage.setTitle("Objective");
        objectiveImageStage.setScene(new Scene(root));
        objectiveImageStage.initOwner(stage);
        /* Even while the objective image is showing, the player can 
        still interact with the main window */
        objectiveImageStage.initModality(Modality.NONE);

        /* The window is shown until the player closes it */
        objectiveImageStage.show();
    }

    /**
     * Show the card that the player drew.
     * 
     * @param card the card that the player drew
     */
    private void showCardDrew(final Card card) {
        /* Get image of the card from CardViewImpl */
        final ImageView cardView = CreateCardView.createCardView(card, 100);
        cardView.setPreserveRatio(true);

        /* Context for the card, show name of the player */
        final Label caption = new Label("Player: " + controller.getCurrentPlayer().getName() + "New card: ");
        caption.setStyle("-fx-font-size: 14px;");

        final VBox root = new VBox(10, cardView, caption);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        final Stage cardImageStage = new Stage();
        cardImageStage.setTitle("Card drawn");
        cardImageStage.setScene(new Scene(root));
        cardImageStage.initOwner(stage);

        cardImageStage.initModality(Modality.APPLICATION_MODAL);
        cardImageStage.showAndWait();
    }

    /**
     * Sets what to do when a player wins / when game is over.
     * 
     * @param winner the winner
     */
    public void showGameOver(final Player winner) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("GAME OVER");
        alert.setHeaderText(null);
        alert.setContentText(winner.getName() + " won!");
        alert.show();
    }
}
