package it.unibo.risiko.view;

import it.unibo.risiko.model.history.History;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * GameLog is a JavaFX {@link ScrollPane} that produces a scrollable list of strings representing the game history.
 */
public class GameLog extends ScrollPane {
    private static final int PADDING = 5;
    //private final History history;
    private final VBox content = new VBox();

    /**
     * @param history the game history to be connected to this class
     */
    public GameLog(History history) {
        super();
        this.setContent(this.content);
        this.content.setPadding(new Insets(PADDING));
        this.content.heightProperty().addListener((a, b, c) -> setVvalue(1));
        this.setFitToWidth(true);
        history.addListener(new ListChangeListener<>() {

            @Override
            public void onChanged(final Change<? extends String> c) {
                while (c.next()) {
                    for (final String string : c.getAddedSubList()) {
                        final Label label = new Label(string);
                        label.setWrapText(true);
                        label.setMaxWidth(Double.MAX_VALUE);
                        label.setPadding(new Insets(PADDING));
                        final Separator separator = new Separator();
                        content.getChildren().addAll(label, separator);
                    }
                }
            }
        });
    }
}
