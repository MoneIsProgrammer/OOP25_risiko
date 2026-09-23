package it.unibo.risiko.view.cards;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import it.unibo.risiko.model.deck.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Create the territory and objective cards view to show to the player
 */

public class CardViewImpl {

    private static final Map<String, Image> cache = new HashMap<>();
    private static final Map<String, Image> objectiveCache = new HashMap<>();

        private static Image loadImage(String fileName) {
            return cache.computeIfAbsent(fileName, f -> {
                URL url = CardViewImpl.class.getResource("/cardimages/" + f);
                if (url == null) {
                    throw new IllegalStateException("Missing card image: " + fileName);
                }
                return  new Image(url.toExternalForm());
            });
        }

        private static Image loadObjectiveImage(String fileName) {
            return objectiveCache.computeIfAbsent(fileName, f -> {
                URL url = CardViewImpl.class.getResource("/objectiveimages/" + f);
                if (url == null) {
                    throw new IllegalStateException("Missing objective image: " + fileName);
                }
                return new Image(url.toExternalForm());
            });
        }

    /**
     * view for the territory cards
     * @param card
     * @param size
     * @return returns the image's view
     */
    public static ImageView createCardView(Card card, double size) {
        
        String fileName = card.getTerritory().getTerritoryId() + "_" + card.getTroop().getTroopName() + ".png";

        Image image = loadImage(fileName);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        /** 
         * when a player clicks a card's image, i can use getUserData() 
         * to get the card and know which card was clicked 
         */

        imageView.setUserData(card);

        return imageView;
    }

    /**
     * view for the objective cards
     * @param card
     * @param size
     * @return
     */
    public static ImageView createObjectiveView(Card card, double size) {
        String fileName = card.getObjective().getObjectiveId() + ".png";

        Image image = loadObjectiveImage(fileName);
        ImageView objectiveImageView = new ImageView(image);
        objectiveImageView.setFitWidth(size);
        objectiveImageView.setFitHeight(size);
        objectiveImageView.setPreserveRatio(true);

        objectiveImageView.setUserData(card);

        return objectiveImageView;
    }

}
