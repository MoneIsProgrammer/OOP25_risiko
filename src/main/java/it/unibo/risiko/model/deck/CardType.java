package it.unibo.risiko.model.deck;

/** 
 * Enum class is being used, as the 
 * card type is fixed.
 */
public enum CardType {
    /** 
     * TERRITORY card is a type of card that has a 
     * territory and a troop represented on it. 
     */
    TERRITORY("Territory"),
    /** 
     * JOLLY card is a type of card that does not 
     * represent any territory, it represents all three 
     * troops (cavalry, infantry and cannon). 
     */
    JOLLY("Jolly"),
    /** 
     * OBJECTIVE card is a type of card that contains 
     * the description of an objective a player must 
     * achieve in order to win. 
     */
    OBJECTIVE("Objective");

    /**
     * The private fields are final, to ensure
     * that they are not modified during runtime.
     */
    private final String cardType;

    /* Card type constructor */
    CardType(final String cardType) {
        this.cardType = cardType;
    }

    /**
     * @return the card type
     */
    public String getCardType() {
        return cardType;
    }
}
