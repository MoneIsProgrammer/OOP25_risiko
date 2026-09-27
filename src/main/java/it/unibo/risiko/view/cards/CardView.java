package it.unibo.risiko.view.cards;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.strategy.StrategyUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardTroops;
import it.unibo.risiko.model.deck.CardType;
import it.unibo.risiko.model.deck.CreateCardView;
import it.unibo.risiko.model.deck.TerritoriesDeck;
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
        var testDeck = new TerritoriesDeck();
        for (int i = 0; i < 42; i++) {
            playerHand.add(testDeck.dealCard());
        }
        /* Each card in the player's hand is displayed */
        for (final Card card : playerHand) {
            final ImageView cardView = CreateCardView.createCardView(card, 100);
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
        okButton.setDisable(!valid(chosenCards));
    }

    private boolean valid(Set<Card> setOfCards) { //shamelessy stolen from card bonus
                /* Variable to store the number of bonus troops */
        int bonus = 0;
        /*
         * Calculate the number of jolly cards in the set
         */
        int nJolly = 0;
        for (final Card card : setOfCards) {
            if (card.getCardType().equals(CardType.JOLLY.getCardType())) {
                nJolly++;
            }
        }
        int nCannons = 0;
        int nCavalry = 0;
        int nInfantry = 0;
        for (final Card card : setOfCards) {
            if (card.getCardType().equals(CardType.TERRITORY.getCardType())) {
                if (card.getTroop() == CardTroops.CANNONS) {
                    nCannons++;
                } else if (card.getTroop() == CardTroops.CAVALRY) {
                    nCavalry++;
                } else if (card.getTroop() == CardTroops.INFANTRY) {
                    nInfantry++;
                }
            }
        }

        /* If the number of jolly cards is more than one, return 0 */
        if (nJolly > 1) {
            return true;
        } else if (nJolly == 1) {
            /* If the number of jolly cards is 1, check whether the other two 
             * cards are of the same troop */
            if (nCannons == 2 || nCavalry == 2 || nInfantry == 2) {
                final int withJollyBonus = 12;
                bonus += withJollyBonus;
            }
        } else if (nJolly == 0) {
            /* No jolly cards, which means:
             * If all three cards are of the same kind, I'll check whether:
             * They're all cannon and 4 extra troops can be deployed;
             * They're all infantry and 6 extra troops can be deployed;
             * They're all cavalry and 8 extra troops can be deployed;
             * If they're each a different type of troop, 10 extra troops 
             * can be deployed;
             */
            if (nCannons == 3) {
                final int cannonBonus = 4;
                bonus += cannonBonus;
            } else if (nInfantry == 3) {
                final int infantryBonus = 6;
                bonus += infantryBonus;
            } else if (nCavalry == 3) {
                final int cavarlyBonus = 8;
                bonus += cavarlyBonus;
            } else if (nCannons == 1 && nInfantry == 1 && nCavalry == 1) {
                final int trisBonus = 10;
                bonus += trisBonus;
            }
        }
        return bonus != 0;
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
