package it.unibo.risiko.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

/**
 * Handles the player turn order and their removal from the game.
 */
public final class PlayerTurn {

    private List<Player> playerOrder = new ArrayList<>();
    private int counter = -1;
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
}
