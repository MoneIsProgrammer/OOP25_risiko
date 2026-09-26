package it.unibo.risiko.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;

import it.unibo.risiko.model.turn.Phase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
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
    private final List<Node> moveSetup;
    private final List<Node> cardSetup;

    /**
     * Default constructor.
     * 
     * @param getStrenght Consumer to get the number of armies
     * @param counter Tracks the number of armies the player has to use or wants to use
     * @param canGenerate if the controller allows to generate an action
     * @param maxArmyforAction the number of troops the players can utilize
     * @param advancePhase advaces the controller phase
     * @param addListener to add a listener when phase changes
     */
    public ChangingBox(
        final Consumer<Integer> getStrenght,
        final IntegerProperty counter, 
        final ReadOnlyBooleanProperty canGenerate, 
        final ReadOnlyIntegerProperty maxArmyforAction,
        final Consumer<PropertyChangeListener> addListener,
        final Runnable advancePhase
    ) {
        super();
        this.counter = counter;
        buttonActive.bind(canGenerate.and(counter.greaterThan(0).and(counter.lessThanOrEqualTo(maxArmyforAction))));

        final var doneButton = new Button("done");
        doneButton.setOnAction(e -> advancePhase.run());

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
            // you can attack again, done goes to the next phase
            this.counter.set(0);
        });
        attackSetup = List.of(
            new Text("attack"), 
            subtractButton, 
            new Text("Armies:"), 
            armiesCounter, 
            addButton, 
            confirmButton, 
            doneButton
        );

        final var armies = new Text();
        armies.textProperty().bind(counter.asString());
        final var reinforceButton = new Button("Confirm");
        reinforceButton.setOnAction(e -> advancePhase.run());
        reinforceButton.disableProperty().bind(counter.isEqualTo(0).not());
        reinforceSetup = List.of(
            new Text("reinforce"), 
            new Text("Armies to place:"), 
            armies, 
            reinforceButton
        );

        // in the move you choose the armies too, with the same buttons of the attack
        moveSetup = List.of(
            new Text("move"), 
            subtractButton, 
            new Text("Armies:"), 
            armiesCounter, 
            addButton, 
            confirmButton, 
            doneButton
        );

        cardSetup = List.of(new Text("card"), doneButton);

        super.getChildren().addAll(reinforceSetup);

        addListener.accept(new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                change((Phase) evt.getNewValue());
            }
        });
    }

    /**
     * Changes the army selection based on the phase.
     * 
     * @param phase determinates which configuration should be applied
     */
    public void change(final Phase phase) { //invoke when phase changes
        super.getChildren().clear();
        switch (phase) {
            case REINFORCE, SETUP:
                super.getChildren().addAll(reinforceSetup);
                break;
            case ATTACK:
                super.getChildren().addAll(attackSetup);
                break;
            case MOVE:
                super.getChildren().addAll(moveSetup);
                break;
            case PLAYCARDS:
                super.getChildren().addAll(cardSetup);
        }
    }
}
