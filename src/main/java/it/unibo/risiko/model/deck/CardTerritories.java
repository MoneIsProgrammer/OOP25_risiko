package it.unibo.risiko.model.deck;

/**
 * Enum class is being used, as the territories
 * in the deck are fixed
 */
public enum CardTerritories {
    AFGHANISTAN("Afghanistan"),
    NORTHAFRICA("North Africa"),
    SOUTHAFRICA("South Africa"),
    EASTAFRICA("East Africa"),
    ALASKA("Alaska"),
    ALBERTA("Alberta"),
    CENTRALAMERICA("Central America"),
    ARGENTINA("Argentina"),
    WESTERNAUSTRALIA("Western Australia"),
    EASTERNAUSTRALIA("Eastern Australia"),
    BRAZIL("Brazil"),
    CHINA("China"),
    CHILE("Chile"),
    CONGO("Congo"),
    EGYPT("Egypt"),
    SOUTHERNEUROPE("Southern Europe"),
    WESTERNEUROPE("Western Europe"),
    NORTHERNEUROPE("Northern Europe"),
    JAPAN("Japan"),
    GREATBRITAIN("Great Britain"),
    GREENLAND("Greenland"),
    INDIA("India"),
    INDONESIA("Indonesia"),
    ICELAND("Iceland"),
    YAKUTIA("Yakutia"),
    KAMCHATKA("Kamchatka"),
    MADAGASCAR("Madagascar"),
    MIDDLEEAST("Middle East"),
    MONGOLIA("Mongolia"),
    NEWGUINEA("New Guinea"),
    ONTARIO("Ontario"),
    PERU("Peru"),
    QUEBEC("Quebec"),
    SCANDINAVIA("Scandinavia"),
    SIAM("Siam"),
    SIBERIA("Siberia"),
    WESTERNAMERICA("Western America"),
    EASTERNAMERICA("Eastern America"),
    NORTHWESTTERRITORIES("Northwest Territories"),
    UKRAINE("Ukraine"),
    URALS("Urals"),
    VENEZUELA("Venezuela");

    /**
     * The private fields are final, to ensure
     * that they are not modified during runtime
     */
    private final String territoryName;

    /* Constructor */
    private CardTerritories(String territoryName) {
        this.territoryName = territoryName;
    }

    /* Public Method - Getters */
    public String getTerritoryName() {
        return territoryName;
    }
}