package it.unibo.risiko.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.turn.Phase;

/**
 * Handles the player turn order and their removal from the game.
 */
public final class PlayerTurn {
    private boolean setupDone = false;
    private List<Player> playerOrder = new ArrayList<>();
    private int counter = -1;
    private Phase currentPhase;

    /**
     * Default constructor, already shuffles the players.
     * 
     * @param roster The players to shuffle
     */
    public PlayerTurn(final Roster roster) {
        playerOrder.addAll(roster.getAllPlayers());
        Collections.shuffle(playerOrder);
    }

    /**
     * Gets the next player in turn order.
     * 
     * @return the next player
     */
    public Player next() {
        counter++;
        if (setupDone) {
            this.currentPhase = Phase.PLAYCARDS;
        }
        return playerOrder.get(counter % playerOrder.size());
    }

    /**
     * Removes a player for from the turn order.
     * 
     * @param player to remove
     */
    public void remove(final Player player) {
        final var current = playerOrder.get(counter % playerOrder.size());
        if (current.equals(player)) {
            throw new IllegalArgumentException("cannot remove the active player");
        }
        if (!playerOrder.remove(player)) {
            throw new IllegalArgumentException("player does not exist");
        }
        counter = playerOrder.indexOf(current);
    }

    /**
     * Advaces to the next phase.
     */
    public void advancePhase() {
        switch (currentPhase) {
            case PLAYCARDS:
                setPhase(Phase.REINFORCE);
                break;
            case REINFORCE:
                setPhase(Phase.ATTACK);
                break;
            case ATTACK:
                setPhase(Phase.MOVE);
                break;
            default:
                //do nothing
                break;
        }
    }

    /**
     * Sets the current game phase.
     * 
     * @param phase the new phase
     */
    public void setPhase(Phase phase) {
        this.currentPhase = phase;
    }

    /**
     * return the current game phase.
     * 
     * @return the current game phase
     */
    public Phase getCurrentPhase() {
        return this.currentPhase;
    }

    public void setupFinished() {
        setPhase(Phase.PLAYCARDS);
        setupDone = true;
    }
}
