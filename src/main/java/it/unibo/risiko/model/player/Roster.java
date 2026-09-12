package it.unibo.risiko.model.player;

import java.util.List;
import java.util.Optional;

/**
 * Rapresents the Players partecipating in the game and give access to them.
 */
public interface Roster {

    /**
     * Searches the player by its ID.
     * 
     * @param playerId the ID to search
     * @return the player with correspondent ID
     */
    Player getPlayer(String playerId);

    /**
     * Searches the player by its assigned color.
     * 
     * @param color the color to search
     * @return the player with the correspontend color, may return {@link Optional#empty()} if the color hasn't been assigned
     */
    Optional<Player> getPlayer(RisikoColors color);

    /**
     * Gives all the partecipating players.
     * 
     * @return a list of players whitch may not follow turn order
     */
    List<Player> getAllPlayers();

}