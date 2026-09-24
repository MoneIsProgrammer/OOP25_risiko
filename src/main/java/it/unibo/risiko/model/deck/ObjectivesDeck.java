package it.unibo.risiko.model.deck;

/**
 * Contains method to create an objectives deck.
 *
 */
public class ObjectivesDeck extends Deck {

    /** 
     * Creates a deck with all the objective cards.
     */
    public void createObjectiveDeck() {
        this.populateObjectiveDeck();
        this.shuffle();
    }

    /**
     * Gets a card containing the objective.
     * 
     * @return an objective card from the avabile ones
     */
    public Card getObjectiveCard() {
        return this.dealObjectiveCard();
    }
}
