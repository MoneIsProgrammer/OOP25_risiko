package it.unibo.risiko.model.deck;

/** This class helps create an object card of one of the three types: 
* 1. Territory Card
* 2. Jolly Card
* 3. Objective Card
* The card has a troop and territory, if it is a Territory Card.
* The card has an objective description, if it's an Objective Card.
* If the card is of type "JOLLY", then the only variable is the cardType,
* as we just need to know it's type to represent a Jolly card (all jolly cards
* have the same visual representation).
*/
public class Card {

    /* Private Fields, each Territory card has a territory and a troop */
    private CardTerritories territory;
    private CardTroops troop;
    /* Private Field, each Objective card has an objective description */
    private Objective objective;
    /* Private Field, specifies the type of the card (Territory, Objective or Jolly) */
    private CardType cardType;

    /** Constructor for territory cards, creates a card with a specified 
     * territory and troop. It also sets the card type, i.e. TERRITORY
     * @param territory is the territory represented on the card
     * @param troop is the troop represented on the card
    */
    public Card(CardTerritories territory, CardTroops troop) {
        this.territory = territory;
        this.troop = troop;
        this.cardType = CardType.TERRITORY;
    }

    /* Constructor for jolly cards, sets the card type as Jolly */
    public Card() {
        this.cardType = CardType.JOLLY;
    }

    /** Constructor for objective cards, sets the card type as OBJECTIVE
     */
    public Card(Objective objective) {
        this.objective = objective;
        this.cardType = CardType.OBJECTIVE;
    }

    /* Getters */
    public CardTerritories getTerritory() {
        return territory;
    }

    public CardTroops getTroop() {
        return troop;
    }

    public Objective getObjective() {
        return objective;
    }

    public String getCardType() {
        return cardType.getCardType();
    }
}
