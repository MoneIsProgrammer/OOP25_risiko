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

/**
 * A box containing the number of armies used in the specific action, changes based on the passed phase 
 * and prevents committing an invalid number of troops.
 */
public class ChangingBox extends HBox {

    private final IntegerProperty counter;
    private final List<Node> attackSetup;
    private final List<Node> reinforceSetup;
    private final BooleanProperty buttonActive = new SimpleBooleanProperty(false);

    /**
     * Default constructor.
     * 
     * @param getStrenght Consumer to get the number of armies
     * @param counter Tracks the number of armies the player has to use or wants to use
     * @param canGenerate if the controller allows to generate an action
     * @param maxArmyforAction the number of troops the players can utilize
     */
    public ChangingBox(
        final Consumer<Integer> getStrenght,
        final IntegerProperty counter, 
        final BooleanProperty canGenerate, 
        final IntegerProperty maxArmyforAction
    ) {
        super();
        this.counter = counter;
        buttonActive.bind(canGenerate.and(counter.greaterThan(-1).and(counter.lessThanOrEqualTo(maxArmyforAction))));
        final var addButton = new Button("+");
        addButton.setOnAction(e -> this.counter.set(this.counter.get() + 1));
        final var subtractButton = new Button("-");
        subtractButton.setOnAction(e -> {
            this.counter.set(this.counter.get() - 1);
        });
        final var armiesCounter = new Text();
        armiesCounter.textProperty().bind(counter.asString());
        final var confirmButton = new Button("confirm");
        confirmButton.disableProperty().bind(buttonActive.not());
        confirmButton.setOnAction(e -> {
            getStrenght.accept(this.counter.get());
            this.counter.set(0);
            this.change(Phase.REINFORCE); //TODO remove when done
        });
        attackSetup = List.of(subtractButton, new Text("Armies:"), armiesCounter, addButton, confirmButton);

        final var armies = new Text();
        armies.textProperty().bind(counter.asString());
        final var reinforceButton = new Button("Confirm");
        reinforceButton.setOnAction(e -> change(Phase.ATTACK)); //TODO Change when done
        reinforceSetup = List.of(new Text("Armies to place:"), armies, reinforceButton);

        super.getChildren().addAll(attackSetup);
    }

    /**
     * Changes the army selection based on the phase.
     * 
     * @param phase determinates which configuration should be applied
     */
    public void change(final Phase phase) { //invoke when phase changes //TODO add all phases
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
