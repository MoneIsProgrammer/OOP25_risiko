package it.unibo.risiko.model.map;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link Territory}.
 * The neighbours are in a LinkedHashSet so they keep the order of the map file,
 * and every run draws them and reports errors the same way.
 */
public final class TerritoryImpl implements Territory {

    private final String id;
    private final String name;
    private final String continentId;
    private final Set<String> adjacent = new LinkedHashSet<>();

    private String ownerId;
    private int armies;

    /**
     * Creates a territory with no owner and no armies.
     *
     * @param id unique id
     * @param name name to show on screen
     * @param continentId the continent it belongs to
     */
    public TerritoryImpl(final String id, final String name, final String continentId) {
        this.id = id;
        this.name = name;
        this.continentId = continentId;
    }

    /**
     * Adds a neighbour. Package private because only {@link GameMapBuilder} should do it,
     * while it builds the map.
     *
     * @param territoryId the id of the neighbour
     */
    void addAdjacent(final String territoryId) {
        this.adjacent.add(territoryId);
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
    public String getContinentId() {
        return this.continentId;
    }

    @Override
    public Set<String> getAdjacentIds() {
        return Collections.unmodifiableSet(this.adjacent);
    }

    @Override
    public Optional<String> getOwnerId() {
        return Optional.ofNullable(this.ownerId);
    }

    @Override
    public void setOwner(final String playerId) {
        this.ownerId = playerId;
    }

    @Override
    public int getArmies() {
        return this.armies;
    }

    @Override
    public void addArmies(final int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can't add a negative number of armies");
        }
        this.armies = this.armies + amount;
    }

    @Override
    public void removeArmies(final int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Can't remove a negative number of armies");
        }
        if (amount > this.armies) {
            throw new IllegalArgumentException(this.id + " only has " + this.armies + " armies");
        }
        this.armies = this.armies - amount;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.armies + ")";
    }
}
