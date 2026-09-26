package it.unibo.risiko.model.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** 
 * Allows the controller to send an alert to the view.
 * In particular, the setWinner method in PlayerTurn 
 * publishes an event and the GameScene subscribes to it 
 * and calls the showGameOver method.
 * 
 * 
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();

    /**
     * The GameOverEvent class is passed as the class to subscribe to.
     * 
     * @param <T> the type of event to be subscribed to
     * @param eventType the class to subscribe to
     * @param eventHandler when GameOverEvent is published, this function is called
     */
    public <T> void subscribe(final Class<T> eventType, final Consumer<T> eventHandler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventHandler);
    }

    /** 
     * Get all the handlers and if the handlers are null, return nothing. 
     * Otherwise, for each handler call them for that event.
     * 
     * @param <T> type of event to be subscribed to
     * @param event the event to be notified of.
     */
    @SuppressWarnings("unchecked")
    public <T> void publish(final T event) {
        final List<Consumer<?>> handlers = subscribers.get(event.getClass());

        if (handlers == null) {
            return;
        }

        for (final Consumer<?> handler : handlers) {
            ((Consumer<T>) handler).accept(event);
        }
    }
}
