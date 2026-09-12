package it.unibo.risiko.model.event;

/**
 * The general interface for a visitor of the Visitor Pattern to interact with {@link Event}.
 * 
 * @param <T> the return type of the implementation.
 */
public interface EventVisitor<T> {
    /**
     * Used to perform operations that need to use the specifics of AttackEvent.
     * 
     * @param event to be visited
     * @return type to be determined in the implementation
     */
    T visit(AttackEvent event);

    /**
     * Used to perform operations that need to use the specifics of AttackResultEvent.
     * 
     * @param event to be visited
     * @return type to be determined in the implementation
     */
    T visit(AttackResultEvent event);

    /**
     * Used to perform operations that need to use the specifics of MoveEvent.
     * 
     * @param event to be visited
     * @return type to be determined in the implementation
     */
    T visit(MoveEvent event);

    /**
     * Used to perform operations that need to use the specifics of ReinforceEvent.
     * 
     * @param event to be visited
     * @return type to be determined in the implementation
     */
    T visit(ReinforceEvent event);

    /**
     * Used to perform operations that need to use the specifics of CardEvent.
     * 
     * @param event to be visited
     * @return type to be determined in the implementation
     */
    T visit(CardEvent event);
}
