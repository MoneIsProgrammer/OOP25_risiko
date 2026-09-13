package it.unibo.risiko.model.common;

/**
 * Anything in the game that has a text id: territories, continents, players, cards.
 * We pass ids around instead of objects, so the model packages don't depend on each other.
 */
// not meant to be used as a lambda, that's why the PMD warning is turned off
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Identifiable {

    /**
     * The id of the element, it never changes during a game.
     *
     * @return the id of the element
     */
    String getId();
}
