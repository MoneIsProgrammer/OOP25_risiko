package it.unibo.risiko.view.cards;

import it.unibo.risiko.model.player.Player;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

/**
 * View for the cards.
 */
public class CardView {
    /* The amount of cards the player has to choose */
    private static final int NUM_CARDS = 3;
    /* List if cards that the player selects to play */
    private final Set<Card> chosenCards = new LinkedHashSet<>();
    /* Button for the player to click when they've chosen a combo */
    private Button okButton;

    /**
     * Creates the combo that can be played.
     * 
     * @param player the current player
     * @return an Optional that contains the combo, or empty if doesn't want to play anything
     */
    public Optional<List<Card>> askComboToPlay(final Player player) {

        /* The hand of the player, who we're asking to play a combo */
        final List<Card> playerHand = player.getHand();

        /* Dialog to display list of cards */
        final Dialog<List<Card>> dialog = new Dialog<>();
        /* Sets the title of the window and the text displayed to the player */
        dialog.setTitle("Play a combo");
        dialog.setHeaderText("Select exactly 3 cards.");
        dialog.setContentText(
            "Press Play when you have selected the cards you want to play, press Cancel if you don't want to play any cards."
        );

        /* FlowPane helps arrange the hand */
        final FlowPane handPane = new FlowPane(10, 10);
        /* Each card in the player's hand is displayed */
        for (final Card card : playerHand) {
            final ImageView cardView = CardViewImpl.createCardView(card, 100);
            /* call setCombo to add the selected/clicked card to the combo */
            cardView.setOnMouseClicked(e -> setCombo(card, cardView));

            handPane.getChildren().add(cardView);
        }

        dialog.getDialogPane().setContent(handPane);

        final ButtonType okType = new ButtonType("Play", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);
        this.okButton = (Button) dialog.getDialogPane().lookupButton(okType);
        okButton.setDisable(true);

        /* check which button (Play/Cancel) was clicked */
        dialog.setResultConverter(bt ->
            bt.equals(okType) ? new ArrayList<>(chosenCards) : null);

        return dialog.showAndWait();
    }

    /**
     * Allows player to select a tris of cards to play before the reinforcement phase.
     * If the card had already been chosen, it gets removed from the list, otherwise if 
     * the number of cards is less than 3, the clicked card is added to the list.
     * 
     * @param card card that the player clicked
     * @param cardView the image of the card
     */
    private void setCombo(final Card card, final ImageView cardView) {
        if (chosenCards.contains(card)) {
            chosenCards.remove(card);
            cardView.setStyle("");
        } else if (chosenCards.size() < NUM_CARDS) {
            chosenCards.add(card);
            cardView.setStyle("-fx-effect: dropshadow(gaussian, gold, 15, 0.5, 0, 0);");
        }
        /* The ok/play button is disabled if the cards selected are not 3, 
         * As the game is set now, the player is allowd to select a card combo even if 
         * it's not one of the following (as they can still gain armies if the territory on 
         * a card is a territory owned by that player):
         * - 3 Cannons
         * - 3 infantry
         * - 3 cavalry
         * - 1 infantry, 1 cannon and 1 cavalry
         * - 1 jolly and two of same troops
         * so we only check that exactly three cards are selected
        */
        okButton.setDisable(chosenCards.size() != 3);
    }

    /**
     * gets the combo of cards chosen by the player.
     * 
     * @return the combo
     */
    public List<Card> getCombo() {
        return new ArrayList<>(chosenCards);
    }
}
