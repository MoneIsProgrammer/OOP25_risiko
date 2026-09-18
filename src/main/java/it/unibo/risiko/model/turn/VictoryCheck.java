package it.unibo.risiko.model.turn;

import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.deck.CardObjectives;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.map.Continent;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;

// TODO: impostare get e set objective per player
/** Purpose: after each turn, check whether a player meets the conditions 
 * for victory
 * NB: This only checks whether the player's objective matches any of the 
 * first 8 objectives (and OBJECTIVEX) within the objectives deck as the 
 * rest of the objectives are managed by the method checkElimination(), that
 * verifies whether a player has been eliminated
 */
public class VictoryCheck {

    /** GameMap's methods cannot be directly used within the class,
     * therefore, we add the following private fields and constructor 
     * to use methods such as getTerritoriesOf(playerId);
     */
    private final GameMap map;

    public VictoryCheck(final GameMap map) {
        this.map = map;
    }

    /**
     * The method receives in input the player's id and checks whether 
     * the victory conditions have been met
     * @param playerId
     * @return returns false if victory conditions have not been met,
     * returns true if victory conditions have been met
     * */
    public boolean victoryCheck(String playerId) {
        /* index for iterating in the for loops */
        int i;
        /* Stores the total number of territories occupied by the player */
        int nOfTerritoriesOccupied = this.map.getTerritoriesOf(playerId).size();
        /* Stores the list of territories occupied by the player */
        Set<Territory> territoriesOccupied = this.map.getTerritoriesOf(playerId);
        /** The victory conditons are achieving the objective, the switch 
         * will check whether the victory conditions are met */
        switch(playerId.getObjective()) {
             /* The objective is "Conquer 18 territories, the territories must have atleast two armies each" */
            case CardObjectives.OBJECTIVE1:
                /* Has the player conquered 18 territories? */
                if ((nOfTerritoriesOccupied) >= 18) {
                    /* Do each of these territories have atleast two armies each? */
                    for (Territory territory: territoriesOccupied) {
                        if (!(territory.getArmies() >= 2)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            /* The objective is "Conquer 24 territories" */
            case CardObjectives.OBJECTIVE2:
                /* Has the player conquered 24 territories? */
                if ((nOfTerritoriesOccupied) >= 24) {
                    return true;
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE3:
                /* The objective is "Conquer all of North America and Africa" */
                if ((playerId.equals(map.getContinentOwner(getContinentId("North America")))) && (playerId.equals(map.getContinentOwner(getContinentId("Africa"))))) {
                    return true;
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE4:
                /* The objective is "Conquer all of North America and Oceania" */
                if ((playerId.equals(map.getContinentOwner(getContinentId("North America")))) && (playerId.equals(map.getContinentOwner(getContinentId("Oceania"))))) {
                    return true;
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE5:
                /* The objective is "Conquer all of Asia and South America" */
                if ((playerId.equals(map.getContinentOwner(getContinentId("South America")))) && (playerId.equals(map.getContinentOwner(getContinentId("Asia"))))) {
                    return true;
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE6:
                /* The objective is "Conquer all of Asia and Africa" */
                if ((playerId.equals(map.getContinentOwner(getContinentId("Asia")))) && (playerId.equals(map.getContinentOwner(getContinentId("Africa"))))) {
                    return true;
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE7:
                /** The objective is "Conquer all of Europe, South America and a third continent of your choice" 
                 * First check whether Europe and South America have been conquered and then check the number of 
                 * continents conquered, if the number of continents conquered is more than or equal to three, it 
                 * means that the player owns continents other than Europe and South America and therefore the 
                 * objective has been achieved
                */
                if ((playerId.equals(map.getContinentOwner(getContinentId("Europe")))) && (playerId.equals(map.getContinentOwner(getContinentId("South America"))))) {
                    if(getNOfContinentsConquered(playerId) >= 3) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVE8:
                /** The objective is "Conquer all of Europe, Oceania and a third continent of your choice" 
                 * First check whether Europe and Oceania have been conquered and then check the number of 
                 * continents conquered, if the number of continents conquered is more than or equal to three, it 
                 * means that the player owns continents other than Europe and Oceania and therefore the 
                 * objective has been achieved
                */
               if ((playerId.equals(map.getContinentOwner(getContinentId("Europe")))) && (playerId.equals(map.getContinentOwner(getContinentId("Oceania"))))) {
                    if(getNOfContinentsConquered(playerId) >= 3) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            case CardObjectives.OBJECTIVEX:
                /** Conquer 24 territories, this objective is set when the player cannot achieve their 
                 * objective due to another player's elimination */
                /** If it doesn't match any of the above objectives, that means the player's objective 
                 * was not achieved
                 */
                /* Has the player conquered 24 territories? */
                if ((nOfTerritoriesOccupied) >= 24) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
        return false;
    }

    /**
     * getContinentId loops through all the continents, until it 
     * finds a continent with the name of the continent whose id
     * we're looking for
     * @param continentName is the name of the continent whose id 
     * we're looking for
     * @return returns the id of the continent
     */
    String getContinentId(String continentName) {

        String continentId = " ";
        for (Continent continent: map.getContinents()) {
            if ((continent.getName()).equals(continentName)) {
                return continent.getId();
            }
        }
        return continentId;
    }    
    /* Calculates the number of continents conquered by the player */
    int getNOfContinentsConquered(String playerId) {
        int nOfContinents = 0;
        /* Loops through all the continents */
        for (Continent continent: map.getContinents()) {
        /** Compare the player's id with that of the continent owner, 
         * if it matches, increase the number of continents conquered 
         * by the player
         */
            if(playerId.equals(map.getContinentOwner(continent.getId()))) {
                nOfContinents++;
            }
        }
        return nOfContinents;
    }
}
