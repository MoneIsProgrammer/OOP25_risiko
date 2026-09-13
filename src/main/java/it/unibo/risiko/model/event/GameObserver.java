package it.unibo.risiko.model.event;

/**
 * Implemented by whoever wants to know what happens in the game, like the map.
 * The history only stores the events, this is what makes the panels redraw by themselves.
 */
@FunctionalInterface
public interface GameObserver {

    /**
     * Called when something happens in the game.
     *
     * @param event what just happened
     */
    void onEvent(Event event);
}
