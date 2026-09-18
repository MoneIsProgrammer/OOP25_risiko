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
    private CardTerritories territoryName;
    private CardTroops troop;
    /* Private Field, each Objective card has an objective description */
    private CardObjectives objectiveDescription;
    /* Private Field, specifies the type of the card (Territory, Objective or Jolly) */
    private CardType cardType;

    /** Constructor for territory cards, creates a card with a specified 
     * territory name and troop. It also sets the card type, i.e. TERRITORY
     * @param territoryName is the name of the territory represented on the card
     * @param troop is the type of troop represented on the card. It can be either
     * cavalry, cannons or infantry.
    */
    public Card(CardTerritories territoryName, CardTroops troop) {
        this.territoryName = territoryName;
        this.troop = troop;
        this.cardType = CardType.TERRITORY;
    }

    /* Constructor for jolly cards, sets the card type as Jolly */
    public Card() {
        this.cardType = CardType.JOLLY;
    }

    /** Constructor for objective cards, sets the card type as OBJECTIVE
     * @param objectiveDescription is the description of an objective
     */
    public Card(CardObjectives objectiveDescription) {
        this.objectiveDescription = objectiveDescription;
        this.cardType = CardType.OBJECTIVE;
    }

    /* Getters */
    public String getTerritoryName() {
        return territoryName.getTerritoryName();
    }

    public String getTroop() {
        return troop.getTroopName();
    }

    public String getObjectiveDescription() {
        return objectiveDescription.getObjective();
    }

    public String getCardType() {
        return cardType.getCardType();
    }

    /**
     * Returns the cards values based on the type of card, i.e.
     * for TERRITORY cards, it returns the territory's name and 
     * troop's name.
     * for OBJECTIVE cards, it returns the description of the objective.
     * for JOLLY cards, it just returns the type of the card
     */
    public String getCard(CardType cardType) {
        switch (cardType) {
            case TERRITORY: return territoryName.getTerritoryName() + " " + troop.getTroopName();
            case OBJECTIVE: return objectiveDescription.getObjective();
            case JOLLY: return cardType.getCardType();
            default: return "Not a card type";
        }
    }
}
