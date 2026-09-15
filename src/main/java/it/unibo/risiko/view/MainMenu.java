package it.unibo.risiko.view;

import java.util.ArrayList;
import java.util.List;

import it.unibo.risiko.controller.GameController;
import it.unibo.risiko.model.player.PlayerRequest;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class MainMenu extends Application{ //test class to lauch scenes

    private Stage stage;
    List<PlayerRequest> list = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Risiko");
        stage.setScene(new MainMenuScene(newGame(), e -> System.out.print("load"), false));
        stage.show();
    }

    public EventHandler<ActionEvent> newGame() {
        return e -> stage.setScene(new PlayerSelectScene(a -> {
            var game = new GameController(a);
            game.start(stage);
        }));
    }   
}
 