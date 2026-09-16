package it.unibo.risiko.view;

import it.unibo.risiko.controller.GameController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainMenu extends Application{ //test class to lauch scenes

    private Stage stage;
    private Rectangle2D screen = Screen.getPrimary().getBounds();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Risiko");
        stage.setScene(new MainMenuScene(newGame(), e -> System.out.print("load"), false));
        this.stage.setWidth(screen.getWidth());
        this.stage.setHeight(screen.getHeight());
        this.stage.setX(screen.getMinX());
        this.stage.setY(screen.getMinY());
        this.stage.setMaximized(true);
        stage.show();
    }

    public EventHandler<ActionEvent> newGame() {
        return e -> stage.setScene(new PlayerSelectScene(a -> {
            var game = new GameController(a);
            game.start(stage);
        }));
    }   
}
 