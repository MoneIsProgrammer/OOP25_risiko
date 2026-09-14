package it.unibo.risiko.model.map;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of {@link Continent}.
 */
public final class ContinentImpl implements Continent {

    private final String id;
    private final String name;
    private final int bonusArmies;
    private final Set<String> territoryIds = new LinkedHashSet<>();

    /**
     * Creates a continent that has no territories yet.
     *
     * @param id unique id
     * @param name name to show on screen
     * @param bonusArmies extra armies for whoever owns all of it
     */
    public ContinentImpl(final String id, final String name, final int bonusArmies) {
        this.id = id;
        this.name = name;
        this.bonusArmies = bonusArmies;
    }

    /**
     * Adds a territory. Package private because continents are only filled by
     * {@link GameMapBuilder}, while it builds the map.
     *
     * @param territoryId the id of the territory to add
     */
    void addTerritory(final String territoryId) {
        this.territoryIds.add(territoryId);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getTerritoryIds() {
        return Collections.unmodifiableSet(this.territoryIds);
    }

    @Override
    public int getBonusArmies() {
        return this.bonusArmies;
    }

    @Override
    public String toString() {
        return this.name + " [+" + this.bonusArmies + "]";
    }
}
