package it.unibo.risiko.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.PlayerRequest.PlayerStrategyRequest;
import it.unibo.risiko.utils.ColorConversion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Scene for the creation of the player roster.
 */
public class PlayerSelectScene extends Scene {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS = 3;
    private static final Color BACKGROUN_COLOR = Color.GREY;

    private final FlowPane top;
    private final BorderPane bottom;
    private final GridPane mainBox;

    private final Button addHuman;
    private final Button addAggressive;
    private final Button addDefensive;
    private final Button addRandom;

    private final Button done;
    private final TextField field;

    private final List<PlayerRequest> players = new ArrayList<>();
    private final List<Color> playerColors = new ArrayList<>();

    private int counter;
    private final Consumer<List<PlayerRequest>> onDone;

    /**
     * This constructor will use predetermined width and heigth for the scene.
     * 
     * @param onDone used to get the resulting player roster
     */
    public PlayerSelectScene(final Consumer<List<PlayerRequest>> onDone) {
        this(onDone, WIDTH, HEIGHT);
    }

    /**
     * This constuctor will create the scene following the specified sizes.
     * 
     * @param onDone used to get the resulting player roster
     * @param width  of the scene
     * @param height of the scene
     */
    public PlayerSelectScene(final Consumer<List<PlayerRequest>> onDone, final int width, final int height) {
        super(new GridPane(), width, height);
        this.onDone = onDone;
        this.top = new FlowPane();
        this.bottom = new BorderPane();
        this.mainBox = (GridPane) this.getRoot();
        this.addHuman = new Button("Human");
        this.addAggressive = new Button("Aggressive");
        this.addDefensive = new Button("Defensive");
        this.addRandom = new Button("Random");
        this.done = new Button("Start new Game");
        this.field = new TextField();
        this.playerColors.addAll(ColorConversion.getAvabileJavaFXColors());
        initializeLayout();
        initializeButtons();
    }

    private void initializeLayout() {
        final var choiceBoxSpacing = 25;
        final var topPercentageHeight = 60;
        final double insetsSpacing = 20;
        final var playerChoiceBox = new HBox(addHuman, addAggressive, addDefensive, addRandom);
        playerChoiceBox.setSpacing(choiceBoxSpacing);
        top.setMaxWidth(Double.MAX_VALUE);
        top.setAlignment(Pos.TOP_CENTER);
        bottom.setMaxWidth(Double.MAX_VALUE);
        mainBox.addRow(0, top);
        mainBox.addRow(1, bottom);
        final var topSize = new RowConstraints();
        topSize.setPercentHeight(topPercentageHeight);
        final var botSize = new RowConstraints();
        botSize.setPercentHeight(100 - topPercentageHeight);
        final var columnSize = new ColumnConstraints();
        columnSize.setPercentWidth(100);
        mainBox.getColumnConstraints().add(columnSize);
        mainBox.getRowConstraints().addAll(topSize, botSize);
        mainBox.setAlignment(Pos.CENTER);
        bottom.setTop(done);
        bottom.setBottom(playerChoiceBox);
        bottom.setCenter(field);
        // bottom.setPadding(new Insets(10));
        playerChoiceBox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(this.done, Pos.TOP_CENTER);
        BorderPane.setMargin(done, new Insets(0, 0, insetsSpacing, 0));
        BorderPane.setMargin(playerChoiceBox, new Insets(0, 0, insetsSpacing, 0));
        BorderPane.setMargin(this.field, new Insets(insetsSpacing));
        BorderPane.setAlignment(this.field, Pos.CENTER);
        top.setBackground(
                new Background(
                        new BackgroundFill(BACKGROUN_COLOR, null, null)));
        bottom.setBackground(
                new Background(
                        new BackgroundFill(BACKGROUN_COLOR, null, null)));
        top.setMaxWidth(Double.MAX_VALUE);
        bottom.setMaxWidth(Double.MAX_VALUE);
        this.field.setPromptText("Nome del giocatore");
        final double fieldHeightMult = 0.5;
        this.field.maxWidthProperty().bind(bottom.widthProperty().multiply(fieldHeightMult));
        final int fieldHeight = 30;
        this.field.setPrefHeight(fieldHeight);

    }

    private void initializeButtons() {
        this.addHuman.setOnAction(e -> addPlayer(PlayerStrategyRequest.HUMAN));
        this.buttSettings(addHuman);

        this.addAggressive.setOnAction(e -> addPlayer(PlayerStrategyRequest.AGGRESSIVE));
        this.buttSettings(addAggressive);

        this.addRandom.setOnAction(e -> addPlayer(PlayerStrategyRequest.RANDOM));
        this.buttSettings(addRandom);

        this.addDefensive.setOnAction(e -> addPlayer(PlayerStrategyRequest.DEFENSIVE));
        buttSettings(this.addDefensive);

        done.setOnAction(e -> finish());
        final double doneWidthMult = 0.9;
        this.done.prefWidthProperty().bind(bottom.widthProperty().multiply(doneWidthMult));
        final double fontMult = 0.2;
        final double doneHeightMult = 0.2;
        this.done.prefHeightProperty().bind(bottom.heightProperty().multiply(doneHeightMult));
        this.done.setDisable(true);
        this.done.fontProperty()
                .bind(addHuman.widthProperty().multiply(fontMult).map(size -> Font.font(size.doubleValue())));

    }

    private void buttSettings(final Button butt) {
        final double widthMult = 0.04;
        butt.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        butt.fontProperty()
                .bind(bottom.widthProperty()
                .multiply(widthMult)
                .map(size -> Font.font(size.doubleValue())));
    }

    private void addPlayer(final PlayerStrategyRequest playerType) {
        if (this.counter >= MAX_PLAYERS) {
            return;
        }
        this.counter++;
        final String nickname = this.field.getText().isBlank() ? playerType.toString() + Integer.toString(this.counter)
                : this.field.getText();
        final Color color = this.playerColors.removeFirst();
        final VBox box = new VBox();
        final Label name = new Label(nickname);
        name.setPrefWidth(Double.MAX_VALUE);
        name.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        box.getChildren().add(name);
        final int spacing = 5;
        box.setBackground(
                new Background(
                        new BackgroundFill(color, new CornerRadii(spacing), Insets.EMPTY)));
        final double fontMult = 0.1;
        name.fontProperty().bind(name.widthProperty().multiply(fontMult).map(size -> Font.font(size.doubleValue())));
        final double prefWidthMult = 0.25;
        box.prefWidthProperty().bind(top.widthProperty().multiply(prefWidthMult));
        final double prefHeightMult = 0.40;
        box.prefHeightProperty().bind(top.heightProperty().multiply(prefHeightMult));
        name.setAlignment(Pos.CENTER);
        name.setPadding(new Insets(10));
        VBox.setMargin(name, new Insets(10, 10, 0, 10));

        box.setBorder(new Border(
                new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(spacing),
                    new BorderWidths(spacing)
                )));
        FlowPane.setMargin(box, new Insets(10));
        box.setOnMouseClicked(
                e -> {
                    final int i = top.getChildren().indexOf(box);
                    top.getChildren().remove(i);
                    final var temp = players.remove(i);
                    this.playerColors.add(ColorConversion.toJavaFxColor(temp.color()));
                    this.counter--;
                    this.done.setDisable(counter < MIN_PLAYERS);
                });
        top.getChildren().add(box);
        players.add(new PlayerRequest(nickname, playerType, ColorConversion.toRisikoColor(color)));
        this.field.setText("");
        this.done.setDisable(counter < MIN_PLAYERS);
    }

    private void finish() {
        this.onDone.accept(players);
    }
}
