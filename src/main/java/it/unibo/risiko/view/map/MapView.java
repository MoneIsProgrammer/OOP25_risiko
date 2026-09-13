package it.unibo.risiko.view.map;

import it.unibo.risiko.model.event.GameObserver;
import it.unibo.risiko.model.player.RisikoColors;
import java.util.Optional;
import java.util.Set;

/**
 * The map as the controller sees it. There are no JavaFX types here, so changing the
 * graphics library only means writing another implementation. That's also why colors are
 * the model's {@code RisikoColors} and territories are passed as ids.
 * It's a {@link GameObserver} because the map redraws itself when something happens.
 */
public interface MapView extends GameObserver {

    /**
     * Sets the color of a player's territories.
     *
     * @param playerId id of the player
     * @param color the player's color
     */
    void setPlayerColor(String playerId, RisikoColors color);

    /**
     * Selects a territory, the previous one gets deselected.
     *
     * @param territoryId id of the territory to select
     * @throws IllegalArgumentException if that territory is not on the map
     */
    void setSelected(String territoryId);

    /**
     * The territory selected right now.
     *
     * @return its id, or empty if nothing is selected
     */
    Optional<String> getSelected();

    /**
     * Removes the selection, if there is one. It's a separate method because
     * {@link #setSelected(String)} only takes territories that exist.
     */
    void clearSelection();

    /**
     * Highlights some territories, for example the ones you can attack from the selected
     * one. An empty set removes the highlight.
     *
     * @param territoryIds ids of the territories to highlight
     */
    void setHighlighted(Set<String> territoryIds);

    /**
     * Adds someone to be told about the clicks on the map.
     *
     * @param listener the listener to add
     */
    void addTerritoryClickListener(TerritoryClickListener listener);

    /**
     * Redraws the map. It already does it by itself after events and resizes, from outside
     * it's only needed when the model changes without an event, like when the territories
     * are dealt at the start.
     */
    void redraw();
}
