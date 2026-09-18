package it.unibo.risiko.model.turn;

/**
 * The phase the turn is in. The turn manager changes it, the map needs it to know what
 * a click is for.
 */
public enum Phase {

    /** Start of the game, everyone places their first armies. */
    SETUP,

    /** Before the reinforcement, you can play a set of cards. */
    PLAYCARDS,

    /** Start of the turn, you place your new armies. */
    REINFORCE,

    /** You attack the territories next to yours. */
    ATTACK,

    /** End of the turn, you can move armies between your territories. */
    MOVE
}
