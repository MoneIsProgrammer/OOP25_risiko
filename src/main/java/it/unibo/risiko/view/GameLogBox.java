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

public class GameLogBox extends VBox{
    
    public GameLogBox(History history) {
        super();
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5),new BorderWidths(5))));
        this.getChildren().addAll(new Text("Past Moves"), new GameLog(history));
        this.setAlignment(Pos.TOP_CENTER);
    }
}
