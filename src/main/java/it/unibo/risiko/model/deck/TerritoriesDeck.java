package it.unibo.risiko.model.deck;

/**
 * This is a sub-class, it is a specialized type of the Deck Class
 * Attributes: cards (already inherited from Deck)
 * Methods: createDeck(), addJollyDeck()
 * 
 * The purpose of this class is to create a Deck with 
 * all the territory cards
 **/
public class TerritoriesDeck extends Deck {

    /* Create the deck with all the territory cards */
    public void createTerritoryDeck () {
        this.populateTerritoryDeck();
        this.shuffle();
    }

    /* Add the Jolly cards to the deck that already contains all
    the territory cards */
    public void addJollyTerritoryDeck () {
        this.addJolly();
        this.shuffle();
    }
    
}
