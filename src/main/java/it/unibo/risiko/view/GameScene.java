package it.unibo.risiko.view;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unibo.risiko.controller.GameController;
import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.controller.MapClickHandler;
import it.unibo.risiko.model.battle.BattleResult;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CreateCardView;
import it.unibo.risiko.model.deck.DrawCard;
import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.turn.AttackPhase;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.utils.ColorConversion;
import it.unibo.risiko.view.map.DiceCanvas;
import it.unibo.risiko.view.map.MapCanvas;
import it.unibo.risiko.view.map.MapLayout;
import it.unibo.risiko.view.map.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Scene to interact with the game.
 */
public class GameScene {
    //unused private static final double WINDOW_WIDTH = 1100;
    //unused private static final double WINDOW_HEIGHT = 700;

    private Stage stage;
    private GameController controller;
    private AttackPhase attackPhase;
    private PlayerTurn turn;
    private DrawCard drawCard;
    // kept here so the controller can pass them the events
    private MapView mapView;
    private final DiceCanvas dice = new DiceCanvas();

    /**
     * To initialise turn to call methods from PlayerTurn
     * @param turn
     */
    @SuppressWarnings("unused")
    private void playerTurn(final PlayerTurn turn) {
        this.turn = turn;
    }

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
        this.mapView = canvas;
        for (final Player player : roster.getAllPlayers()) {
            mapView.setPlayerColor(player.getId(), player.getColor());
        }

        // clicks for the attack, the move and the reinforce
        final var clickHandler = new MapClickHandler(map, mapView);
        mapView.addTerritoryClickListener(clickHandler);
        clickHandler.addChoiceListener((from, to) -> {
            // the controller gives them to the strategy of the player
            controller.getTerritories(from, to);
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
        for (final Player player : roster.getAllPlayers()) {
            var text = new Label(player.getName());
            text.setFont(new Font(20));
            text.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            var hbox = new HBox(text);
            hbox.setPadding(new Insets(5));
            hbox.setBackground(new Background(new BackgroundFill(ColorConversion.toJavaFxColor(player.getColor()), null, null)));
            box.getChildren().addAll(hbox, new Separator());
        }
        
        /** Button that when clicked, shows the objective card of the player */
        Button objectiveButton = new Button("Show objective");
        /** Gets the objective of the player */
        // turn is still null here, the controller knows who plays
        Card card = controller.getCurrentPlayer().getObjective();

        objectiveButton.setOnAction(e -> {
            showObjectiveWindow(card);
        });

        Button drawButton = new Button("Draw card");
        
        if ((turn.getCurrentPhase().equals(Phase.MOVE)) && (attackPhase.canDraw())) {
            drawButton.setOnAction(e -> {
                Card cardDrew;
                cardDrew = drawCard.drawNewCard();
                showCardDrew(cardDrew);
            });
        }

        // the dice of the last attack, under the players
        box.getChildren().add(dice);
        box.setAlignment(Pos.CENTER);
        final var spacing = 5;
        final var bottom = new ChangingBox(controller.getStrenght, controller.armyCounter, controller.ableToBuild, controller.maxArmyforAction, a -> controller.addListener(a) , () -> controller.advancePhase());
        bottom.setAlignment(Pos.CENTER);
        bottom.setSpacing(spacing);
        box.getChildren().addAll(bottom, new GameLogBox(history), objectiveButton, drawButton);

        // the map follows the size of the window
        final var container = new Pane(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        root.setCenter(container);
        root.setRight(box);
        stage.setScene(new Scene(root));
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

    public GameScene(GameController controller) {
        super();
        this.controller = controller;
    }

    /**
     * shows a window that displays the objective card of the player
     * @param card the objective of the player
     */
    private void showObjectiveWindow (Card card) {
        /** Gets the image of the objective card from CardViewImpl */
        ImageView objectiveView = CreateCardView.createObjectiveView(card, 100);
        objectiveView.setPreserveRatio(true);

        /* Context for the objective, shows name of the player whose objective is being shown */
        Label caption = new Label("Player: " + controller.getCurrentPlayer().getName());
        caption.setWrapText(true);
        caption.setStyle("-fx-font-size: 14px;");

        /* Center the image */
        VBox root = new VBox(10, objectiveView, caption);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Stage objectiveImageStage = new Stage();
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
     * show the card that the player drew
     * @param card the card that the player drew
     */
    private void showCardDrew (Card card) {
        /* Get image of the card from CardViewImpl */
        ImageView cardView = CreateCardView.createCardView(card, 100);
        cardView.setPreserveRatio(true);

        /* Context for the card, show name of the player */
        Label caption = new Label("Player: " + controller.getCurrentPlayer().getName() + "New card: ");
        caption.setStyle("-fx-font-size: 14px;");

        VBox root = new VBox(10, cardView, caption);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        Stage cardImageStage = new Stage();
        cardImageStage.setTitle("Card drawn");
        cardImageStage.setScene(new Scene(root));
        cardImageStage.initOwner(stage);

        cardImageStage.initModality(Modality.APPLICATION_MODAL);
        cardImageStage.showAndWait();
    }

    /**
     * sets what to do when a player wins / when game is over
     * @param winner
     */
    public void showGameOver (Player winner) {   
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("GAME OVER");
        alert.setHeaderText(null);
        alert.setContentText(winner.getName() + " won!");
        alert.show();
    }

}
