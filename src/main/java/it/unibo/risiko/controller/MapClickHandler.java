package it.unibo.risiko.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.view.map.MapView;
import it.unibo.risiko.view.map.TerritoryClickListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Handles the clicks on the map. It doesn't attack, move or place anything, it only says
 * which territories were picked: two in the attack and in the move, one in the reinforce
 * and in the setup.
 */
public final class MapClickHandler implements TerritoryClickListener {

    private final GameMap map;
    private final MapView mapView;
    private final List<BiConsumer<String, String>> listeners = new ArrayList<>();
    private final List<Consumer<String>> placementListeners = new ArrayList<>();

    private String currentPlayer;
    private Phase currentPhase = Phase.SETUP;

    /**
     * Creates the handler, then it has to be added to the map as a listener.
     *
     * @param map the map
     * @param mapView the map on screen
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // the view is shared on purpose
    public MapClickHandler(final GameMap map, final MapView mapView) {
        this.map = map;
        this.mapView = mapView;
    }

    /**
     * Says who is playing now.
     *
     * @param playerId id of the player
     */
    public void setPlayer(final String playerId) {
        this.currentPlayer = playerId;
        clear();
    }

    /**
     * Says the phase, while playing the cards the clicks do nothing.
     *
     * @param phase the phase of the turn
     */
    public void setPhase(final Phase phase) {
        this.currentPhase = phase;
        clear();
    }

    /**
     * Clicks do nothing until the next player is set, for example while a bot plays.
     */
    public void ignoreClicks() {
        this.currentPlayer = null;
        clear();
    }

    /**
     * Adds a listener, it gets the ids of the source and of the destination.
     *
     * @param listener the listener to add
     */
    public void addChoiceListener(final BiConsumer<String, String> listener) {
        this.listeners.add(listener);
    }

    /**
     * Adds a listener for the reinforce and the setup, it gets the id of the territory
     * where the armies go.
     *
     * @param listener the listener to add
     */
    public void addPlacementListener(final Consumer<String> listener) {
        this.placementListeners.add(listener);
    }

    @Override
    public void onTerritoryClicked(final String territoryId) {
        if (!canClick()) {
            return;
        }

        // reinforce and setup, one click is enough: your territory, where the armies go
        if (isPlacing()) {
            if (isMine(this.map.getTerritory(territoryId))) {
                this.mapView.setSelected(territoryId);
                for (final var listener : this.placementListeners) {
                    listener.accept(territoryId);
                }
            }
            return;
        }

        final var source = this.mapView.getSelected();

        // first click, your territory with at least 2 armies
        if (source.isEmpty()) {
            final var clicked = this.map.getTerritory(territoryId);
            if (isMine(clicked) && clicked.getArmies() > 1) {
                this.mapView.setSelected(territoryId);
                this.mapView.setHighlighted(destinations(clicked));
            }
            return;
        }

        // second click, if it's wrong we start again
        if (destinations(this.map.getTerritory(source.get())).contains(territoryId)) {
            for (final var listener : this.listeners) {
                listener.accept(source.get(), territoryId);
            }
        }
        clear();
    }

    @Override
    public void onEmptyClicked() {
        if (canClick()) {
            clear();
        }
    }

    // only the player of the turn, and not while playing the cards
    private boolean canClick() {
        return this.currentPlayer != null
                && (this.currentPhase == Phase.ATTACK || this.currentPhase == Phase.MOVE || isPlacing());
    }

    // in the reinforce and in the setup the clicks say where the armies go
    private boolean isPlacing() {
        return this.currentPhase == Phase.REINFORCE || this.currentPhase == Phase.SETUP;
    }

    // the territory is of the player of the turn
    private boolean isMine(final Territory territory) {
        final var owner = territory.getOwnerId();
        return owner.isPresent() && owner.get().equals(this.currentPlayer);
    }

    // attack: enemy neighbours, move: your connected territories
    private Set<String> destinations(final Territory source) {
        final var found = new HashSet<String>();
        if (this.currentPhase == Phase.ATTACK) {
            for (final String id : source.getAdjacentIds()) {
                final var owner = this.map.getTerritory(id).getOwnerId();
                if (owner.isPresent() && !owner.get().equals(this.currentPlayer)) {
                    found.add(id);
                }
            }
        } else {
            for (final Territory territory : this.map.getTerritoriesOf(this.currentPlayer)) {
                final var id = territory.getId();
                if (!id.equals(source.getId())
                        && this.map.areConnected(source.getId(), id, this.currentPlayer)) {
                    found.add(id);
                }
            }
        }
        return found;
    }

    // removes selection and highlight
    private void clear() {
        this.mapView.clearSelection();
        this.mapView.setHighlighted(Set.of());
    }
}
