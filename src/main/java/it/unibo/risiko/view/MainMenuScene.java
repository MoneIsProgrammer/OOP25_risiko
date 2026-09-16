package it.unibo.risiko.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MainMenuScene extends Scene{
    
    private static final double WIDTH = 500;
    private static final double HEIGHT = 500;
    private final VBox main;
    private final Button newGameButton;
    private final Button loadGameButton;
    private final Button exitButton;
    private final Label title;

    /**
     * @param newGame Action that the New Game button will do
     * @param loadGame  Action that the Load Game Button will do
     * @param canLoadGame If Load Game Button can be pressed, in this case if there is a previous game to load
     */
    public MainMenuScene(final EventHandler<ActionEvent> newGame, final EventHandler<ActionEvent> loadGame, final boolean canLoadGame) {
        this(newGame, loadGame, canLoadGame, WIDTH, HEIGHT);
    }

    public MainMenuScene(final EventHandler<ActionEvent> newGame, final EventHandler<ActionEvent> loadGame,final boolean canLoadGame, final double width, final double height) {
        super(new VBox(), width, height);
        this.main = (VBox) this.getRoot();
        this.title = new Label("RiSiKo!");
        this.title.fontProperty()
                .bind(main.heightProperty()
                .multiply(0.15)
                .map(size -> Font.font(size.doubleValue())));
        this.newGameButton = new Button("new game");
        this.newGameButton.setOnAction(newGame);
        this.loadGameButton = new Button("load game");
        this.loadGameButton.setOnAction(loadGame);
        this.loadGameButton.setDisable(!canLoadGame);
        this.exitButton = new Button("Exit");
        this.exitButton.setOnAction(e -> System.exit(0));
        this.customize(exitButton);
        this.customize(newGameButton);
        this.customize(loadGameButton);
        this.main.getChildren().addAll(this.title, this.newGameButton, this.loadGameButton, this.exitButton);
        this.main.setAlignment(Pos.CENTER);
        this.main.spacingProperty().bind(this.main.heightProperty().multiply(0.1));;
        
    }

    private void customize(final Button button) {
        //
        final double widthMult = 0.04;
        button.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);        
        button.fontProperty()
                .bind(main.heightProperty()
                .multiply(widthMult)
                .map(size -> Font.font(size.doubleValue())));
        button.setPadding(new Insets(10));
    }
}
