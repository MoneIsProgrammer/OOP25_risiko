package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.ObjectivesDeck;
import it.unibo.risiko.model.deck.TerritoriesDeck;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

public class SetupPhase {

    private ObjectivesDeck objectiveDeck = new ObjectivesDeck();
    private TerritoriesDeck territoryDeck = new TerritoriesDeck();
    private final Roster players;

    public SetupPhase(ObjectivesDeck objectiveDeck, final Roster players) {
        this.objectiveDeck = objectiveDeck;
        this.players = players;
    }

    /* Player order is already set by PlayerTurn */
    /* Player color has already been chosen */

    /** The setup phase requires a deck of Territory cards and a deck of Objective cards, 
     * each player is granted one objective card and an evenly divided number of territory 
     * cards, 
     * therefore, create an objectives deck and deal one objective card to each player
     * and create a territory deck and deal territory cards to each player
     */
    public void setObjectivesDeck() {
        this.objectiveDeck.createObjectiveDeck();
    }

    public void dealObjectiveCards() {
        for(Player player: players.getAllPlayers()) {
            player.setObjective(objectiveDeck.setObjectiveCard());
        }
    }

    /* Populate the territory deck with all territory cards, no jolly cards */
    public void setTerritoryDeck() {
        this.territoryDeck.createTerritoryDeck();
    }

    /* Deal the territory cards */
    public void dealTerritoryCards() {
        /* Counter to assure that when dealing cards during the setup phase, 
        it stops after dealing 42 cards */
        int cardsDealt = 0;
        while (cardsDealt < 42) {
            for (Player player: players.getAllPlayers()) {
                if (cardsDealt < 42) {   
                    player.addCard(territoryDeck.dealCard());
                    cardsDealt++;
                }
            }
        }
    }
}