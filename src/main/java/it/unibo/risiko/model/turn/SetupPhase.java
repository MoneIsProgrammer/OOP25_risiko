package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.ObjectivesDeck;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

public class SetupPhase {

    private ObjectivesDeck objectiveDeck = new ObjectivesDeck();
    private final Roster players;

    public SetupPhase(ObjectivesDeck objectiveDeck, final Roster players) {
        this.objectiveDeck = objectiveDeck;
        this.players = players;
    }

    /** The setup phase first requires giving an objective card to each player, 
     * therefore, create an objectives deck and deal an objective card to each 
     * player
     */
    public void setObjectivesDeck() {
        this.objectiveDeck.createObjectiveDeck();
    }

    public void dealObjectiveCards() {
        for(Player player: players.getAllPlayers()) {
            player.setObjective(objectiveDeck.setObjectiveCard());
        }
    }

    
    
}