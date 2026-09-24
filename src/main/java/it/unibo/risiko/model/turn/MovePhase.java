package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.Objective;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.strategy.HumanStrategy;

/**
 * Models the move phase of the game.
 */
public class MovePhase {
    private final GameMap map;
    private final Roster players;
    private final VictoryCheck check;
    private final PlayerTurn turn;
    private Player currentPlayer;
    //private PlayerStrategy strategy;
    //private HumanStrategy humanStrategy;
    private boolean isCompleted;
    //private Player winner;

    /**
     * Default constructor.
     * 
     * @param map the game map
     * @param players the roster of players
     * @param check the checker for victory
     * @param turn the current turn
     */
    public MovePhase(final GameMap map, final Roster players, final VictoryCheck check, final PlayerTurn turn) {
        this.map = map;
        this.players = players;
        this.check = check;
        this.turn = turn;
    }

    /**
     * Checks if the phase is done.
     */
    public void phaseStart() {
        moveArmies();
        if (checkElimination()) {
            final var winner = currentPlayer;
            turn.setWinner(winner);
        }

        isCompleted = true;
    }

    /** 
     * To check if current phase is completed.
     * 
     * @return true if the phase is completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Moves armies.
     */
    void moveArmies() {
        currentPlayer = turn.getCurrentPlayer();
        if (!currentPlayer.isHuman()) {
            currentPlayer.move();
        } else {
            final var strategy = currentPlayer.getStrategy();
            final var humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateMove()) {
                currentPlayer.move();
            }
        }
    }

    /** 
     * After the player has moved armies between territories, 
     * check whether they've achieved their objective or has a 
     * player been eliminated.
     * 
     * @return true if a player has been eliminated
     */
    public boolean checkElimination() {
        for (final Player player: players.getAllPlayers()) {
            if (map.getTerritoriesOf(player.getId()).isEmpty()) {
                /* Check whether a player has been eliminated and if 
                 * the current player's objective was to eliminate this 
                 * player, then it returns true, which means the current 
                 * player has completed their objective and won the game.
                 * 
                 */
                if (managePlayerElimination(player, currentPlayer)) {
                    return true;
                }
            }
        }
        /* If none of the players got eliminated or if a player got eliminated 
         * and this was not the objective of the current player, check if the 
         * player has achieved their objective
        */
        return check.victoryCheck(currentPlayer);
    }

    /**
     * Manages the elimination of a player, by checking whether their 
     * elimination was someone's objective.
     * If it was the objective of another player, changes the objective 
     * of said player
     * In all the other cases, returns false
     * 
     * @param playerEliminated the eliminated player
     * @param actualPlayer the acting player
     * @return If it was the objective of the current player, returns true
     */
    boolean managePlayerElimination(final Player playerEliminated, final Player actualPlayer) {
        final RisikoColors eliminatedColor = playerEliminated.getColor();

        /* objectiveEliminateColor() checks whether a player's objective was to eliminate 
         * eliminated color's troop, if the current player's objective was to eliminate the 
         * eliminated player -> current player wins */
        if (objectiveEliminateColor(actualPlayer, eliminatedColor)) {
            return true;
        }

        /* Loop through all the players and check whether a player's (a player other tha current player) 
         * objective was to eliminate eliminated colour, if it was set a new objective (conquer 24 
         * territories) for that player
         */
        for (final Player player: players.getAllPlayers()) {
            if (!player.equals(actualPlayer) && objectiveEliminateColor(player, eliminatedColor)) {
                player.setNewObjective();
            }
        }

        /* At this point, the current player's objective was not eliminating eliminated colour, therefore, 
        return false */
        return false;
    }

    /**
     * check whether the player's objective was eliminating eliminated colour.
     * 
     * @param player the player for whose objective is being checked
     * @param eliminatedColor the color of the eliminated player
     * 
     * @return returns false if eliminating the colour was not the player's objective,
     *      returns true if it was the player's objective
     */
    boolean objectiveEliminateColor(final Player player, final RisikoColors eliminatedColor) {
        final Card playerObjective = player.getObjective();
        /* controllo se l'obiettivo del player era eliminare quel colore */
        return
            eliminateColorObjective(playerObjective.getObjective())
            && playerTargetColour(playerObjective.getObjective()) == eliminatedColor;
    }

    /** 
     * Check whether a player's objective was between objectives 9 and 14, 
     * i.e. to eliminate a certain colour troop
     * 
     * @param objective the objective to check
     * @return true if it was any of the colour objectives false otherwise
     */
    boolean eliminateColorObjective(final Objective objective) {
        return objective == Objective.OBJECTIVE9
        || objective == Objective.OBJECTIVE10 
        || objective == Objective.OBJECTIVE11 
        || objective == Objective.OBJECTIVE12 
        || objective == Objective.OBJECTIVE13 
        || objective == Objective.OBJECTIVE14;
    }

    /**
     *  Returns the colour of the troop who the player has to eliminate.
     * 
     * @param objective the objective of the player
     * 
     * @return the color of the target
     */
    RisikoColors playerTargetColour(final Objective objective) {
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
