package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardTerritories;
import it.unibo.risiko.model.deck.ObjectivesDeck;
import it.unibo.risiko.model.deck.TerritoriesDeck;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.controller.PlayerTurn;

/**
 * This is the phase before the game starts, initial cards, territories, 
 * army positioning, etc are set
 * SetupPhase
 */
public class SetupPhase {

    private ObjectivesDeck objectiveDeck = new ObjectivesDeck();
    private TerritoriesDeck territoryDeck = new TerritoriesDeck();
    private final Roster players;
    private final PlayerTurn phase;
    private final GameMap map;

    public SetupPhase(ObjectivesDeck objectiveDeck, final Roster players, final PlayerTurn phase, final GameMap map) {
        this.objectiveDeck = objectiveDeck;
        this.players = players;
        this.phase = phase;
        this.map = map;
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
    public void setInitialTerritoryDeck() {
        this.territoryDeck.createTerritoryDeck();
    }

    /* Deal the territory cards */
    public void dealInitialTerritoryCards() {
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

    /** The startingForces method in player already 
     * gives starting forces to each player, subtract the 
     * amount of territories initially assigned to the player,
     * as each territory needs one army at setup */
    public void setStartingForces() {
        for (Player player: players.getAllPlayers()) {
            player.setArmies(player.getStartingForces() - player.getHand().size());
        }
    }

    /** Assign the player's id to the territory's owner id, so that each territory that the 
     * player got has the player's id associated
     */
    public void setInitialTerritories() {
        for (Player player: players.getAllPlayers()) {
            for (Card card: player.getHand()) {
                setTerritoryOwner(card.getTerritory().getTerritoryName(), player.getId());                
            }
        }
    }

    /**
     * Given the territory assigned to a player, sets the player as the owner 
     * of that territory
     * 
     * @param playerTerritoryName name of a territory assigned to the player
     * @param playerId id of the player
     */
    private void setTerritoryOwner(String playerTerritoryName, String playerId) {
        for (CardTerritories territories: CardTerritories.values()) {
            if ((territories.getTerritoryName()).equals(playerTerritoryName)) {
                    map.getTerritory(playerTerritoryName).setOwner(playerId);
            }
        }
    }

    // TODO: posizionamento di tre armate alla volta (le armate sono prese dalla dotazione iniziale)
    public void armiesPlacement() {
        for (Player player: players.getAllPlayers()) {
            //
        }
    }

    /** The armies have been positioned, the territory deck is repopulated 
     * this time it contains the jolly cards too
     * This deck will now be used for the rest of the game
     */
    public void setTerritoryDeck() {
        this.territoryDeck.createTerritoryDeck();
        this.territoryDeck.addJollyTerritoryDeck();
    }

    void setUpFinished(){
        phase.setupFinished();
    }
}