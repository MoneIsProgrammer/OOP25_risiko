package it.unibo.risiko.view.cards;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import it.unibo.risiko.model.deck.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardViewImpl {

    private static final Map<String, Image> cache = new HashMap<>();

        private static Image loadImage(String fileName) {
            return cache.computeIfAbsent(fileName, f -> {
                URL url = CardViewImpl.class.getResource("/cardimages/" + f);
                if (url == null) {
                    throw new IllegalStateException("Missing card image: " + fileName);
                }
                return  new Image(url.toExternalForm());
            });
    }

    public static ImageView createCardView(Card card, double size) {
        
        String fileName = card.getTerritory().getTerritoryId() + "_" + card.getTroop().getTroopName() + ".png";

        // TODO: add/load images to/from classpath, the images must be in src/main/../resources/cardimages/...

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

}
