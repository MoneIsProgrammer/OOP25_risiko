package it.unibo.risiko.model.deck;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.controller.PlayerTurn;

/**
 * Deals a card to the current player.
 */
public class DrawCard {
    private final PlayerTurn turn;
    /* Deck not currently populated, when player calls drawCard, it's automatically populated thanks to dealCard */
    private TerritoriesDeck territoriesDeck = new TerritoriesDeck();

    /**
     * Needs the turn.
     *
     * @param turn the turn
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // on purpose
    public DrawCard(final PlayerTurn turn) {
        this.turn = turn;
    }

    /**
     * Adds a card to the current player.
     * 
     * @return the card dealt
     */
    public Card drawNewCard() {
        final var player = turn.getCurrentPlayer();
        final Card card = territoriesDeck.dealCard();
        player.addCard(card);
        return card;
    }

}
