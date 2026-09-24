package it.unibo.risiko.model.deck;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Create the territory and objective cards view to show to the player.
 */

public final class CreateCardView {

    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final Map<String, Image> OBJECTIVE_CACHE = new HashMap<>();

    private CreateCardView() {
        //private constructor change if this class is not utility
    }

        private static Image loadImage(final String fileName) {
            return CACHE.computeIfAbsent(fileName, f -> {
                final URL url = CreateCardView.class.getResource("/cardimages/" + f);
                if (url == null) {
                    throw new IllegalStateException("Missing card image: " + fileName);
                }
                return new Image(url.toExternalForm());
            });
        }

        private static Image loadObjectiveImage(final String fileName) {
            return OBJECTIVE_CACHE.computeIfAbsent(fileName, f -> {
                final URL url = CreateCardView.class.getResource("/objectiveimages/" + f);
                if (url == null) {
                    throw new IllegalStateException("Missing objective image: " + fileName);
                }
                return new Image(url.toExternalForm());
            });
        }

    /**
     * view for the territory cards.
     * 
     * @param card the territory card
     * @param size the preferred size
     * @return returns the image's view
     */
    public static ImageView createCardView(final Card card, final double size) {

        final String fileName = card.getTerritory().getTerritoryId() + "_" + card.getTroop().getTroopName() + ".png";

        final Image image = loadImage(fileName);
        final ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        /* 
         * when a player clicks a card's image, i can use getUserData() 
         * to get the card and know which card was clicked 
         */

        imageView.setUserData(card);

        return imageView;
    }

    /**
     * view for the objective cards.
     * 
     * @param card the objective card
     * @param size the preferred size
     * @return an image of the card
     */
    public static ImageView createObjectiveView(final Card card, final double size) {
        final String fileName = card.getObjective().getObjectiveId() + ".png";

        final Image image = loadObjectiveImage(fileName);
        final ImageView objectiveImageView = new ImageView(image);
        objectiveImageView.setFitWidth(size);
        objectiveImageView.setFitHeight(size);
        objectiveImageView.setPreserveRatio(true);

        objectiveImageView.setUserData(card);

        return objectiveImageView;
    }

}
