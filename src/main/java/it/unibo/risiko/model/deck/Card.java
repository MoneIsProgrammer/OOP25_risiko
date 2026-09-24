package it.unibo.risiko.model.deck;

/** 
 * This class helps create an object card of one of the three types: 
 * 1. Territory Card
 * 2. Jolly Card
 * 3. Objective Card
 * The card has a troop and territory, if it is a Territory Card.
 * The card has an objective description, if it's an Objective Card.
 * If the card is of type "JOLLY", then the only variable is the cardType,
 * as we just need to know it's type to represent a Jolly card (all jolly cards
 * have the same visual representation).
 */
public final class Card {

    /* Private Fields, each Territory card has a territory and a troop */
    private CardTerritories territory;
    private CardTroops troop;
    /* Private Field, each Objective card has an objective description */
    private Objective objective;
    /* Private Field, specifies the type of the card (Territory, Objective or Jolly) */
    private final CardType cardType;

    /** 
     * Constructor for territory cards, creates a card with a specified 
     * territory and troop. It also sets the card type, i.e. TERRITORY
     * 
     * @param territory is the territory represented on the card
     * @param troop is the troop represented on the card
     */
    public Card(final CardTerritories territory, final CardTroops troop) {
        this.territory = territory;
        this.troop = troop;
        this.cardType = CardType.TERRITORY;
    }

    /**
     *  Constructor for jolly cards, sets the card type as Jolly.
     */
    public Card() {
        this.cardType = CardType.JOLLY;
    }

    /** 
     * Constructor for objective cards, sets the card type as OBJECTIVE.
     * 
     * @param objective the objective to be set
     */
    public Card(final Objective objective) {
        this.objective = objective;
        this.cardType = CardType.OBJECTIVE;
    }

    /**
     * @return the territory on the card
     */
    public CardTerritories getTerritory() {
        return territory;
    }

    /**
     * @return the troop on the card
     */
    public CardTroops getTroop() {
        return troop;
    }

    /**
     * @return the objective on the card
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * @return the type of the card
     */
    public String getCardType() {
        return cardType.getCardType();
    }
}
