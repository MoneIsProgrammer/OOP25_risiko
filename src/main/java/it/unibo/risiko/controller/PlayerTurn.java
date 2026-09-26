package it.unibo.risiko.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.risiko.model.event.GameOverObserver;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.turn.Phase;

/**
 * Handles the player turn order and their removal from the game.
 */
public final class PlayerTurn {
    private boolean setupDone;
    private final List<Player> playerOrder = new ArrayList<>();
    private int counter = -1;
    private Phase currentPhase = Phase.SETUP;
    private final PropertyChangeSupport phaseChange = new PropertyChangeSupport(this);
    // separate from the phase one, the phase listeners cast everything to Phase
    private final PropertyChangeSupport playerChange = new PropertyChangeSupport(this);
    /*Initially gets the current player when the playerOrder is decided, 
     * when it's the next player's turn, it changes current player to the new player
    */
    private Player currentPlayer;
    private GameOverObserver observer;
    private Player winner;

    /**
     * Default constructor, already shuffles the players.
     * 
     * @param roster The players to shuffle
     */
    public PlayerTurn(final Roster roster) {
        playerOrder.addAll(roster.getAllPlayers());
        Collections.shuffle(playerOrder);
        currentPlayer = playerOrder.getFirst();
    }

    /**
     * Gets the next player in turn order, and notifies listeners.
     *
     * @return the next player
     */
    public Player next() {
        if (isGameOver()) {
            return winner;
        } else {
            counter++;
            final Player old = this.currentPlayer;
            currentPlayer = playerOrder.get(counter % playerOrder.size());
            if (setupDone) {
                // with setPhase the listeners know it too
                setPhase(Phase.PLAYCARDS);
            }
            // says who plays now, the map needs it for the clicks
            playerChange.firePropertyChange("player", old, currentPlayer);
            return playerOrder.get(counter % playerOrder.size());
        }
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
    public void setPhase(final Phase phase) {
        final var old = this.currentPhase;
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
     * Gets the current player.
     * 
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * In the move phase, the checkElimination method, 
     * checks whether a player has been eliminated and 
     * manages that player's elimination. After that it 
     * calls the victoryCheck method to check if the 
     * current player has reached their objective, if they 
     * have, checkElimination returns true and the current 
     * player is set as winner 
     * Informs the view through GameOverObserver that the 
     * game is over.
     * 
     * @param winner the player that reached their objective
     */
    public void setWinner(final Player winner) {
        this.winner = winner;
        if (observer != null) {
            observer.onGameOver(winner);
        }
    }

    /**
     * Checks if a winner has been set, if yes, 
     * it means the game is over.
     * 
     * @return returns true if a winner has been set
     */
    public boolean isGameOver() {
        return winner != null;
    }

    /**
     * Getter for the player that won.
     * 
     * @return the winning player
     */
    public Player getWinner() {
        return winner;
    }

    /** 
     * Observer that alerts view when the game is over.
     * 
     * @param observerToAdd the observer to register
     */
    public void setGameOverObserver(final GameOverObserver observerToAdd) {
        this.observer = observerToAdd;
    }

    /**
     * Finishes the setup and lets turn procede.
     */
    public void setupFinished() {
        setPhase(Phase.PLAYCARDS);
        setupDone = true;
        // during the setup the map was not listening yet, so i say again who plays
        playerChange.firePropertyChange("player", null, currentPlayer);
    }

    /**
     * Adds someone to be told when the player of the turn changes.
     * 
     * @param listener the listener to add
     */
    public void addPhaseChangeListener(final PropertyChangeListener listener) {
        phaseChange.addPropertyChangeListener(listener);
    }

    /**
     * Adds someone to be told when the player of the turn changes.
     *
     * @param listener the listener to add
     */
    public void addPlayerChangeListener(final PropertyChangeListener listener) {
        playerChange.addPropertyChangeListener(listener);
    }
}
