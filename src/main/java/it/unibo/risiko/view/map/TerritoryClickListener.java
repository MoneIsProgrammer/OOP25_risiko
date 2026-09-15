package it.unibo.risiko.view.map;

/**
 * Told which territory the user clicked on the {@link MapView}.
 * The map doesn't know the rules, the listener is the one that decides if the move is allowed.
 */
@FunctionalInterface
public interface TerritoryClickListener {

    /**
     * Called when the user clicks on a territory.
     *
     * @param territoryId id of the territory that was pressed
     */
    void onTerritoryClicked(String territoryId);

    /**
     * Called when the click misses every territory, handy to cancel a selection.
     */
    default void onEmptyClicked() {
        // most listeners don't care about clicks on nothing
    }
}
