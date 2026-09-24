package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardTerritories;
import it.unibo.risiko.model.deck.ObjectivesDeck;
import it.unibo.risiko.model.deck.TerritoriesDeck;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

/**
 * This is the phase before the game starts, initial cards, territories, 
 * army positioning, etc are set.
 */
public class SetupPhase {

    private final ObjectivesDeck objectiveDeck;
    private final TerritoriesDeck territoryDeck = new TerritoriesDeck();
    private final Roster players;
    private final GameMap map;
    private final PlayerStrategy strategy;
    //private HumanStrategy humanStrategy;
    private boolean completed;

    /**
     * Default constructor.
     * 
     * @param objectiveDeck the objective deck
     * @param players the roster of players
     * @param map the map of the game
     * @param strategy the strategy of current player
     */
    public SetupPhase(
        final ObjectivesDeck objectiveDeck,
        final Roster players, 
        final GameMap map, 
        final PlayerStrategy strategy
    ) {
        this.objectiveDeck = objectiveDeck;
        this.players = players;
        this.map = map;
        this.strategy = strategy;
    }

    /**
     * Starts the phase.
     */
    public void phaseStart() {
        setObjectivesDeck();
        dealObjectiveCards();
        setInitialTerritoryDeck();
        dealInitialTerritoryCards();
        setStartingForces();
        setInitialTerritories();
        armiesPlacement();
        setTerritoryDeck();

        completed = true;
    }

    /**
     * @return true if phase is completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /* Player order is already set by PlayerTurn */
    /* Player color has already been chosen */

    /** 
     * The setup phase requires a deck of Territory cards and a deck of Objective cards, 
     * each player is granted one objective card and an evenly divided number of territory 
     * cards, 
     * therefore, create an objectives deck and deal one objective card to each player
     * and create a territory deck and deal territory cards to each player.
     */
    private void setObjectivesDeck() {
        this.objectiveDeck.createObjectiveDeck();
    }

    private void dealObjectiveCards() {
        for (final Player player: players.getAllPlayers()) {
            player.setObjective(objectiveDeck.getObjectiveCard());
        }
    }

    /* Populate the territory deck with all territory cards, no jolly cards */
    private void setInitialTerritoryDeck() {
        this.territoryDeck.createTerritoryDeck();
    }

    /* Deal the territory cards */
    private void dealInitialTerritoryCards() {
        /* Counter to assure that when dealing cards during the setup phase, 
        it stops after dealing 42 cards */
        int cardsDealt = 0;
        final int maxNOfCards = 42;
        while (cardsDealt < maxNOfCards) {
            for (final Player player: players.getAllPlayers()) {
                if (cardsDealt < maxNOfCards) {
                    player.addCard(territoryDeck.dealCard());
                    cardsDealt++;
                }
            }
        }
    }

    /** 
     * The startingForces method in player already 
     * gives starting forces to each player, subtract the 
     * amount of territories initially assigned to the player,
     * as each territory needs one army at setup.
     */
    private void setStartingForces() {
        for (final Player player: players.getAllPlayers()) {
            player.setArmies(player.getArmies() - player.getHand().size());
        }
    }

    /** 
     * Assign the player's id to the territory's owner id, so that each territory that the 
     * player got has the player's id associated.
     */
    private void setInitialTerritories() {
        for (final Player player: players.getAllPlayers()) {
            for (final Card card: player.getHand()) {
                setTerritoryOwner(card.getTerritory().getTerritoryName(), player.getId());
            }
            /* now that the territories have been assigned to the player, clear the player's hand */
            player.getHand().clear();
        }
    }

    /**
     * Given the territory assigned to a player, sets the player as the owner 
     * of that territory.
     * 
     * @param playerTerritoryName name of a territory assigned to the player
     * @param playerId id of the player
     */
    private void setTerritoryOwner(final String playerTerritoryName, final String playerId) {
        for (final CardTerritories territories: CardTerritories.values()) {
            if (territories.getTerritoryName().equals(playerTerritoryName)) {
                    map.getTerritory(playerTerritoryName).setOwner(playerId);
            }
        }
    }

    /**
     * Allows players to set up armies in their territories
     * Calls different methods for placement, based on whether 
     * the player is human or ai.
     */
    private void armiesPlacement() {
        for (final Player player: players.getAllPlayers()) {
            if (!player.isHuman()) {
                player.setupPlacement();
            } else {
                //strategy = player.getStrategy();
                final var humanStrategy = (HumanStrategy) this.strategy;
                if (humanStrategy.canCreateReinforce()) {
                    player.setupPlacement();
                }
            }
        }
    }

    /** 
     * The armies have been positioned, the territory deck is repopulated 
     * this time it contains the jolly cards too
     * This deck will now be used for the rest of the game.
     */
    private void setTerritoryDeck() {
        this.territoryDeck.createTerritoryDeck();
        this.territoryDeck.addJollyTerritoryDeck();
    }
}
