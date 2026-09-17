package it.unibo.risiko.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

public class PlayerTurn {

    List<Player> playerOrder = new ArrayList<>();
    int counter = -1;
    
    public PlayerTurn(final Roster roster) {
        playerOrder.addAll(roster.getAllPlayers());
        Collections.shuffle(playerOrder);
    }

    public Player next() {
        counter++;
        return playerOrder.get(counter % playerOrder.size());
    }

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
