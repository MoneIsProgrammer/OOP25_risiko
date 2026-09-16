package it.unibo.risiko.view;

import java.util.List;
import java.util.function.Consumer;

import it.unibo.risiko.model.turn.Phase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ChangingBox extends HBox {
    
    private IntegerProperty counter;
    private List<Node> attackSetup;
    private List<Node> reinforceSetup;
    private BooleanProperty buttonActive = new SimpleBooleanProperty(false);

    public ChangingBox(Consumer<Integer> getStrenght, IntegerProperty counter, BooleanProperty canGenerate, IntegerProperty maxArmyforAction) {
        super();
        this.counter = counter;
        buttonActive.bind(canGenerate.and(counter.greaterThan(-1).and(counter.lessThanOrEqualTo(maxArmyforAction))));
        var addButton = new Button("+");
        addButton.setOnAction(e -> this.counter.set(this.counter.get() + 1));
        var subtractButton = new Button("-");
        subtractButton.setOnAction(e -> {
            this.counter.set(this.counter.get() - 1);
        });
        var armiesCounter = new Text();
        armiesCounter.textProperty().bind(counter.asString());
        var confirmButton = new Button("confirm");
        confirmButton.disableProperty().bind(buttonActive.not());
        confirmButton.setOnAction(e -> {
            getStrenght.accept(this.counter.get());
            this.counter.set(0);
            this.change(Phase.REINFORCE);//change
        });
        attackSetup = List.of(subtractButton, new Text("Armies:"), armiesCounter, addButton, confirmButton);

        var armies = new Text();
        armies.textProperty().bind(counter.asString());
        var reinforceButton = new Button("Confirm");
        reinforceButton.setOnAction(e -> change(Phase.ATTACK));
        reinforceSetup = List.of(new Text("Armies to place:"),armies,reinforceButton);

        super.getChildren().addAll(attackSetup);
    }

    /**
     * Changes the army selection based on the phase
     * 
     * @param phase determinates which configuration should be applied
     */
    public void change(Phase phase) {//invoke when phase changes
        super.getChildren().clear();
        switch (phase) {
            case REINFORCE:
                super.getChildren().addAll(reinforceSetup);
                break;
        
            default:
                super.getChildren().addAll(attackSetup);
                break;
        }
        
    }
}
