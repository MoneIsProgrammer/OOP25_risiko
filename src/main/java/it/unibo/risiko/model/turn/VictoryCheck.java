package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.deck.CardObjectives;
import it.unibo.risiko.model.map.GameMap;

// TODO: completare il controllo delle condizioni di vittoria
/** Purpose: after each turn, check whether a player meets the conditions 
 * for victory
 */
public class VictoryCheck {

    /** GameMap methods cannot be directly used within the class,
     * therefore, we add the following private field and constructor 
     * to use methods such as getTerritoriesOf(playerId);
     */
    private final GameMap map;

    public VictoryCheck(final GameMap map) {
        this.map = map;
    }

    /**
     * The method receives in input the player's id
     * @param playerId
     * @return tells whether the victory conditions have been met
     *
    public String victoryCheck(String playerId) {
        /** The victory conditons are achieving the objective, the switch 
         * will check whether the victory conditions are met 
        switch(getObjective(playerId)) {
             /** The objective is "Conquer 18 territories, the territories must have atleast two troops each" 
            case CardObjectives.OBJECTIVE1:
                /* Has the player conquered 18 territories 
                if ((this.map.getTerritoriesOf(playerId).size()) > 17) {

                }
            default:
                //
        }
        
        return "No";
    }*/
}
