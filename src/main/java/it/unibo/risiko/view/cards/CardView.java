/*package it.unibo.risiko.view.cards;

import it.unibo.risiko.model.deck.Card;
import javafx.scene.image.ImageView;

public class CardView {

    ImageView cardView = CardViewImpl.createCardView(card, 100);
    Card card = (Card) cardView.getUserData();
    private final int NUM_CARDS = 3;

    cardView.setOnMouseClicked(e -> {
        if (selected.contains(card)) {
            selected.remove(card);
            cardView.setStyle("");
        } else if (selected.size() < MIN_CARDS) {
            selected.add(card);
            cardView.setStyle("-fx-effect: dropshadow(gaussian, gold, 15, 0.5, 0, 0);");
        }
        updatePlayButtonState();
    });

}*/
