package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardObjectives;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.turn.VictoryCheck;

public class MovePhase {
    private final GameMap map;
    private final Roster players;
    private final VictoryCheck check;
    // TODO: remove after rest of the move phase is implemented
    private Player currentPlayer;

    public MovePhase (final GameMap map, final Roster players, final VictoryCheck check) {
        this.map = map;
        this.players = players;
        this.check = check;
    }
    // TODO: insert rest of stuff for move phase

    boolean res = false;
    /** After the player has moved armies between territories, 
     * check whether they've achieved their objective or has a 
     * player been eliminated
     */

    public void checkElimination() {
        for (Player player: players.getAllPlayers()) {
            if (map.getTerritoriesOf(player.getId()).size() <= 0) {
                /** Check whether a player has been eliminated and if 
                 * the current player's objective was to eliminate this 
                 * player, then it returns true, which means the current 
                 * player has completed their objective and won the game.
                 */
                if (managePlayerElimination(player)) {
                    // TODO: Cosa fare in caso di vittoria?
                }
            }
        }
    }

    /** If none of the players got eliminated or if a player got eliminated 
     * and this was not the objective of the current player, check if the 
     * player has achieved their objective
    */
    {
        res = check.victoryCheck(currentPlayer.getId());
    }

    boolean managePlayerElimination(Player playerEliminated){
        for (Player player: players.getAllPlayers()) {
            switch (player.getObjective()) {
                /** The objective is "Destroy all the Yellow troops. Note: If the Yellow troops are not present 
                     * in the game, or if your own troops are Yellow, or if the Yellow troops are 
                     * eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE9:
                    /** If the player who got eliminated was YELLOW, check whether the objective 
                     * to eliminate YELLOW troops was the current player's,
                     * if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.YELLOW)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                /** The objective is "Destroy all the Red troops. Note: If the Red troops are not present 
                 * in the game, or if your own troops are Red, or if the Red troops are 
                 * eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE10:
                    /** If the player who got eliminated was RED, check whether the objective 
                     * to eliminate RED troops was the current player's:
                     * - if yes, return true
                     * - if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.RED)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                /** The objective is "Destroy all the Green troops. Note: If the Green troops are not present " +
                "in the game, or if your own troops are Green, or if the Green troops are " +
                "eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE11:
                    /** If the player who got eliminated was GREEN, check whether the objective 
                     * to eliminate GREEN troops was the current player's:
                     * - if yes, return true
                     * - if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.GREEN)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                /** The objective is "Destroy all the Blue troops. Note: If the Blue troops are not present 
                 * in the game, or if your own troops are Blue, or if the Blue troops are 
                 * eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE12:
                    /** If the player who got eliminated was BLUE, check whether the objective 
                     * to eliminate BLUE troops was the current player's:
                     * - if yes, return true
                     * - if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.BLUE)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                /** The objective is "Destroy all the Pink troops. Note: If the Pink troops are not present 
                 * in the game, or if your own troops are Pink, or if the Pink troops are 
                 * eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE13:
                    /** If the player who got eliminated was PINK, check whether the objective 
                     * to eliminate PINK troops was the current player's:
                     * - if yes, return true
                     * - if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.PINK)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                /** The objective is "Destroy all the Black troops. Note: If the Black troops are not present 
                 * in the game, or if your own troops are Black, or if the Black troops are 
                 * eliminated by another player, your objective becomes conquering 24 territories." */
                case CardObjectives.OBJECTIVE14:
                    /** If the player who got eliminated was BLACK, check whether the objective 
                     * to eliminate BLACK troops was the current player's:
                     * - if yes, return true
                     * - if not, then change the objective of the player whose objective was to 
                     * eliminate the player to conquer 24 territories
                     */
                    if ((playerEliminated.getColor()).equals(RisikoColors.BLACK)) {
                        if (player.getId().equals(currentPlayer.getId())) {
                            return true;
                        } else {
                            player.setNewObjective(CardObjectives.OBJECTIVEX);
                        }
                    }
                default:
                    /* Nothing to do in any of the other cases */
            }
        }
        /** At the end of the for loop, if none of the conditions returned true, then the 
         * player elimination was not the current player's objective and if it was the 
         * objective of someone other than the current player, then the objective was changed.
         */
        return false;
    }
}
