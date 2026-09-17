package it.unibo.risiko.view;

import it.unibo.risiko.model.history.History;
import javafx.geometry.Pos;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Box to wrap GameLog and gives it a title.
 */
public final class GameLogBox extends VBox {
    private static final int DEF_SPACING = 5;

    /**
     * Default constructor.
     * 
     * @param history the history to link to GameLog
     */
    public GameLogBox(final History history) {
        super();
        super.setBorder(new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(DEF_SPACING), new BorderWidths(DEF_SPACING))
        ));
        super.getChildren().addAll(new Text("Past Moves"), new GameLog(history));
        super.setAlignment(Pos.TOP_CENTER);
    }
}
