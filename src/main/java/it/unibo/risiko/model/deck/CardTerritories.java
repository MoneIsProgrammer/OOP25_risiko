package it.unibo.risiko.model.deck;

/**
 * Enum class is being used, as the territories
 * in the deck are fixed.
 */
public enum CardTerritories {
    AFGHANISTAN("Afghanistan", "afghanistan"),
    NORTHAFRICA("North Africa", "north_africa"),
    SOUTHAFRICA("South Africa", "south_africa"),
    EASTAFRICA("East Africa", "east_africa"),
    ALASKA("Alaska", "alaska"),
    ALBERTA("Alberta", "alberta"),
    CENTRALAMERICA("Central America", "central_america"),
    ARGENTINA("Argentina", "argentina"),
    WESTERNAUSTRALIA("Western Australia", "western_australia"),
    EASTERNAUSTRALIA("Eastern Australia", "eastern_australia"),
    BRAZIL("Brazil", "brazil"),
    CHINA("China", "china"),
    IRKUTSK("Irkutsk", "irkutsk"),
    CONGO("Congo", "congo"),
    EGYPT("Egypt", "egypt"),
    SOUTHERNEUROPE("Southern Europe", "southern_europe"),
    WESTERNEUROPE("Western Europe", "western_europe"),
    NORTHERNEUROPE("Northern Europe", "northern_europe"),
    JAPAN("Japan", "japan"),
    GREATBRITAIN("Great Britain", "great_britain"),
    GREENLAND("Greenland", "greenland"),
    INDIA("India", "india"),
    INDONESIA("Indonesia", "indonesia"),
    ICELAND("Iceland", "iceland"),
    YAKUTIA("Yakutia", "yakutia"),
    KAMCHATKA("Kamchatka", "kamchatka"),
    MADAGASCAR("Madagascar", "madagascar"),
    MIDDLEEAST("Middle East", "middle_east"),
    MONGOLIA("Mongolia", "mongolia"),
    NEWGUINEA("New Guinea", "new_guinea"),
    ONTARIO("Ontario", "ontario"),
    PERU("Peru", "peru"),
    QUEBEC("Quebec", "quebec"),
    SCANDINAVIA("Scandinavia", "scandinavia"),
    SIAM("Siam", "siam"),
    SIBERIA("Siberia", "siberia"),
    WESTERNAMERICA("Western America", "western_america"),
    EASTERNAMERICA("Eastern America", "eastern_america"),
    NORTHWESTTERRITORIES("Northwest Territories", "northwest_territories"),
    UKRAINE("Ukraine", "ukraine"),
    URALS("Urals", "urals"),
    VENEZUELA("Venezuela", "venezuela");

    /**
     * The private fields are final, to ensure
     * that they are not modified during runtime.
     */
    private final String territoryName;
    private final String id;

    /* Constructor */
    CardTerritories(final String territoryName, final String id) {
        this.territoryName = territoryName;
        this.id = id;
    }

    /**
     * @return the name of the territory
     */
    public String getTerritoryName() {
        return territoryName;
    }

    /**
     * @return the territory id
     */
    public String getTerritoryId() {
        return id;
    }
}
