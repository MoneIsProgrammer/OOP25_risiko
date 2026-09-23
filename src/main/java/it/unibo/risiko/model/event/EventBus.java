package it.unibo.risiko.model.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Allows the controller to send an alert to the view.
 * In particular, the setWinner method in PlayerTurn 
 * publishes an event and the GameScene subscribes to it 
 * and calls the showGameOver method
 * EventBus
 * 
*/
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();
    
    /**
     * the GameOverEvent class is passed as the class to subscribe to
     * @param <T>
     * @param eventType the class to subscribe to
     * @param eventHandler when GameOverEvent is published, this function is called
     */
    public <T> void subscribe(Class<T> eventType, Consumer<T> eventHandler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventHandler);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        /** get all the handlers and if the handlers are null, return nothing. 
         * Otherwise, for each handler call them for that event
        */
        List<Consumer<?>> handlers = subscribers.get(event.getClass());

        if (handlers == null) {
            return;
        }

        for (Consumer<?> handler : handlers) {
            ((Consumer<T>) handler).accept(event);
        }
    }
}
