package it.unibo.risiko.model.turn;

import java.util.List;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

// TODO: completare il controllo delle condizioni di vittoria
/** Purpose: after each turn, check whether a player meets the conditions 
 * for victory
 */
public class VictoryCheck {
    private final Roster roster;
    
    public VictoryCheck(final Roster roster) {
        this.roster = roster;
    }

    public String victoryCheck() {
        List<Player> players = this.roster.getAllPlayers();
        for (Player player: players) {
            /* controllo per ogni giocatore */
        }
        return "No";
    }
}
