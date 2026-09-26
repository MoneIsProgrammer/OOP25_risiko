package it.unibo.risiko.model.deck;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.Player;

public class DrawCard {
    private Player player;
    private PlayerTurn turn;
    /* Deck not currently populated, when player calls drawCard, it's automatically populated thanks to dealCard */
    TerritoriesDeck territoriesDeck = new TerritoriesDeck();

    public Card drawNewCard() {
        player = turn.getCurrentPlayer();
        Card card = territoriesDeck.dealCard();
        player.addCard(card);
        return card;
    }

}
