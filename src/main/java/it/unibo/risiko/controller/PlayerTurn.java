package it.unibo.risiko.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    private Phase currentPhase = Phase.SETUP;
    private final PropertyChangeSupport phaseChange = new PropertyChangeSupport(this);


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
     * Gets the current player
     * 
     * @return the current player
     */
    public Player currentPlayer() {
        return playerOrder.get(counter);
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
     * Advaces to the next phase,and notifies listeners.
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
     * Sets the current game phase, and notifies listeners.
     * 
     * @param phase the new phase
     */
    public void setPhase(Phase phase) {
        var old = this.currentPhase;
        this.currentPhase = phase;
        phaseChange.firePropertyChange("phase", old, this.currentPhase);
    }

    /**
     * return the current game phase.
     * 
     * @return the current game phase
     */
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Finishes the setup and lets turn procede
     */
    public void setupFinished() {
        setPhase(Phase.PLAYCARDS);
        setupDone = true;
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        phaseChange.addPropertyChangeListener(listener);
    }
}
