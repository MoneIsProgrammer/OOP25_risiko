package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.Objective;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

public class MovePhase{
    private final GameMap map;
    private final Roster players;
    private final VictoryCheck check;
    private PlayerTurn turn;
    private Player currentPlayer = turn.getCurrentPlayer();
    private PlayerStrategy strategy;
    private HumanStrategy humanStrategy;
    private boolean isCompleted = false;

    public void phaseStart() {
        moveArmies();
        if (checkElimination()) {
            // TODO: player ha vinto, cosa faccio?
        }

        isCompleted = true;
    }

    public boolean isCompleted () {
        return isCompleted;
    }

    public MovePhase (final GameMap map, final Roster players, final VictoryCheck check) {
        this.map = map;
        this.players = players;
        this.check = check;
    }
    // TODO: the player can move his armies from one of his territories to another
    void moveArmies() {
        if (!(currentPlayer.isHuman())) {
            currentPlayer.move();
        } else {
            strategy = currentPlayer.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            humanStrategy.moveDestination(null);
            humanStrategy.moveSource(null);
            humanStrategy.moveStrenght(0);
            humanStrategy.getMoveAfterConquest(null, null, currentPlayer);
        }
    }


    /** After the player has moved armies between territories, 
     * check whether they've achieved their objective or has a 
     * player been eliminated
     */

    public boolean checkElimination() {
        for (Player player: players.getAllPlayers()) {
            if (map.getTerritoriesOf(player.getId()).size() <= 0) {
                /** Check whether a player has been eliminated and if 
                 * the current player's objective was to eliminate this 
                 * player, then it returns true, which means the current 
                 * player has completed their objective and won the game.
                 * 
                 */
                return (managePlayerElimination(player, currentPlayer));
            }
        }
        /** If none of the players got eliminated or if a player got eliminated 
         * and this was not the objective of the current player, check if the 
         * player has achieved their objective
        */
        return (check.victoryCheck(currentPlayer));
    }


    /**
     * Manages the elimination of a player, by checking whether their 
     * elimination was someone's objective
     * If it was the objective of the current player, returns true
     * If it was the objective of another player, changes the objective 
     * of said player
     * In all the other cases, returns false
     */
    boolean managePlayerElimination(Player playerEliminated, Player currentPlayer){
        RisikoColors eliminatedColor = playerEliminated.getColor();

        /** objectiveEliminateColor() checks whether a player's objective was to eliminate 
         * eliminated color's troop, if the current player's objective was to eliminate the 
         * eliminated player -> current player wins */
        if (objectiveEliminateColor(currentPlayer, eliminatedColor)) {
            return true;
        }

        /** Loop through all the players and check whether a player's (a player other tha current player) 
         * objective was to eliminate eliminated colour, if it was set a new objective (conquer 24 
         * territories) for that player
         */
        for (Player player: players.getAllPlayers()) {
            if ( !((player).equals(currentPlayer)) && objectiveEliminateColor(player, eliminatedColor) ) {
                player.setNewObjective();
            }
        }

        /* At this point, the current player's objective was not eliminating eliminated colour, therefore, 
        return false */
        return false;
    }

    /**
     * check whether the player's objective was eliminating eliminated colour
     * @param player the player for whose objective is being checked
     * @param eliminatedColor the color of the eliminated player
     * @return returns false if eliminating the colour was not the player's 
     * objective, returns true if it was the player's objective
     */
    boolean objectiveEliminateColor(Player player, RisikoColors eliminatedColor) {
        Card playerObjective = player.getObjective();
        /* controllo se l'obiettivo del player era eliminare quel colore */
        return (eliminateColorObjective(playerObjective.getObjective())) && (playerTargetColour(playerObjective.getObjective()) == eliminatedColor);
    }

    /** Check whether a player's objective was between objectives 9 and 14, 
     * i.e. to eliminate a certain colour troop
     * @return returns true if it was any of the colour objectives
     * returns false otherwise
     */
    boolean eliminateColorObjective(Objective objective) {
        return ( (objective.equals(Objective.OBJECTIVE9)) || (objective.equals(Objective.OBJECTIVE10)) || (objective.equals(Objective.OBJECTIVE11)) || (objective.equals(Objective.OBJECTIVE12)) || (objective.equals(Objective.OBJECTIVE13)) || (objective.equals(Objective.OBJECTIVE14)) );
    }

    /* Returns the colour of the troop who the player has to eliminate */
    RisikoColors playerTargetColour(Objective objective) {
        switch (objective) {
            case OBJECTIVE9:
                return RisikoColors.YELLOW;
            case OBJECTIVE10:
                return RisikoColors.RED;
            case OBJECTIVE11:
                return RisikoColors.GREEN;
            case OBJECTIVE12:
                return RisikoColors.BLUE;
            case OBJECTIVE13:
                return RisikoColors.PINK;
            case OBJECTIVE14:
                return RisikoColors.BLACK;
            default:
                throw new IllegalArgumentException("colour does not match");
        }
    }
}
