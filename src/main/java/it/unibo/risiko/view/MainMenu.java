package it.unibo.risiko.view;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.controller.GameController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main menu of the application.
 */
public final class MainMenu extends Application {

    private Stage mainStage;
    private final Rectangle2D screen = Screen.getPrimary().getBounds();

    @Override
    
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
    public void start(final Stage stage) throws Exception {
        this.mainStage = stage;
        this.mainStage.setTitle("Risiko");
        this.mainStage.setScene(new MainMenuScene(newGame(), null, false));
        this.mainStage.setWidth(screen.getWidth());
        this.mainStage.setHeight(screen.getHeight());
        this.mainStage.setX(screen.getMinX());
        this.mainStage.setY(screen.getMinY());
        this.mainStage.setMaximized(true);
        this.mainStage.show();
    }

    /**
     * Creates the event for new game button.
     * 
     * @return event to launch when clicking new game
     */
    public EventHandler<ActionEvent> newGame() {
        return e -> mainStage.setScene(new PlayerSelectScene(a -> {
            final var game = new GameController(a, mainStage);
            game.registerView(new GameScene(game));
        }));
    }
}
