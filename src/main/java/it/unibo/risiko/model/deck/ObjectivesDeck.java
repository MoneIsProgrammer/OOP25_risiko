package it.unibo.risiko.model.deck;

/**
 * Contains method to create an objectives deck
 * ObjectivesDeck
 */
public class ObjectivesDeck extends Deck {

    /* Creates a deck with all the objective cards */
    void createObjectiveDeck() {
        this.populateObjectiveDeck();
        this.shuffle();
    }
}
