package it.unibo.risiko.model.deck;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.player.Player;

public class DrawCard {
    private Player player;
    private PlayerTurn turn;
    /* Deck not currently populated, when player calls drawCard, it's automatically populated thanks to dealCard */
    TerritoriesDeck territoriesDeck = new TerritoriesDeck();

    /**
     * Needs the turn.
     *
     * @param turn the turn
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // on purpose
    public DrawCard(final PlayerTurn turn) {
        this.turn = turn;
    }

    public Card drawNewCard() {
        player = turn.getCurrentPlayer();
        Card card = territoriesDeck.dealCard();
        player.addCard(card);
        return card;
    }

}
