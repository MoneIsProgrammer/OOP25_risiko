package it.unibo.risiko.model.deck;

/**
 * Enum class is being used as the troops 
 * in the deck are fixed.
 */
public enum CardTroops {
    CANNONS("Cannons"),
    INFANTRY("Infantry"),
    CAVALRY("Cavalry");

    private final String troopsName;

    /* Constructor */
    CardTroops(final String troopsName) {
        this.troopsName = troopsName;
    }

    /**
     * @return the troop name
     */
    public String getTroopName() {
        return troopsName;
    }
}
