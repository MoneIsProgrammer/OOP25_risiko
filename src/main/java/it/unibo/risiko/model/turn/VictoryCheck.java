package it.unibo.risiko.model.turn;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.map.Continent;
import it.unibo.risiko.model.player.Player;

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
     * The method checks whether a player has met their
     * victory conditions
     * @param player the player for whose victory condition is being checked
     * @return returns false if victory conditions have not been met,
     * returns true if victory conditions have been met
     * */
    public boolean victoryCheck(Player player) {
        String playerId = player.getId();
        Card playerObjective = player.getObjective();

        final int territoriesTwentyFour = 24;
        final int territoriesEighteen = 18;
        /* Stores the total number of territories occupied by the player */
        int nOfTerritoriesOccupied = this.map.getTerritoriesOf(playerId).size();
        /* Stores the list of territories occupied by the player */
        Set<Territory> territoriesOccupied = this.map.getTerritoriesOf(playerId);
        /** The victory conditons are achieving the objective, the switch 
         * will check whether the victory conditions are met */
        switch(playerObjective.getObjective()) {
             /* The objective is "Conquer 18 territories, the territories must have atleast two armies each" */
            case OBJECTIVE1:
                /* Has the player conquered 18 territories? */
                if ((nOfTerritoriesOccupied) >= territoriesEighteen) {
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
            case OBJECTIVE2:
                /* Has the player conquered 24 territories? */
                return ((nOfTerritoriesOccupied) >= territoriesTwentyFour);
            case OBJECTIVE3:
                /* The objective is "Conquer all of North America and Africa" */
                return (ownsContinent(playerId, "north_america") && ownsContinent(playerId, "africa"));
            case OBJECTIVE4:
                /* The objective is "Conquer all of North America and Oceania" */
                return (ownsContinent(playerId, "north_america") && ownsContinent(playerId, "oceania"));
            case OBJECTIVE5:
                /* The objective is "Conquer all of Asia and South America" */
                return (ownsContinent(playerId, "asia") && ownsContinent(playerId, "south_america"));
            case OBJECTIVE6:
                /* The objective is "Conquer all of Asia and Africa" */
                return (ownsContinent(playerId, "asia") && ownsContinent(playerId, "africa"));
            case OBJECTIVE7:
                /** The objective is "Conquer all of Europe, South America and a third continent of your choice" 
                 * First check whether Europe and South America have been conquered and then check the number of 
                 * continents conquered, if the number of continents conquered is more than or equal to three, it 
                 * means that the player owns continents other than Europe and South America and therefore the 
                 * objective has been achieved */
                return (ownsContinent(playerId, "europe") && ownsContinent(playerId, "south_america") && (getNOfContinentsConquered(playerId) >=3));
            case OBJECTIVE8:
                /** The objective is "Conquer all of Europe, Oceania and a third continent of your choice" 
                 * First check whether Europe and Oceania have been conquered and then check the number of 
                 * continents conquered, if the number of continents conquered is more than or equal to three, it 
                 * means that the player owns continents other than Europe and Oceania and therefore the 
                 * objective has been achieved */
                return (ownsContinent(playerId, "europe") && ownsContinent(playerId, "oceania") && (getNOfContinentsConquered(playerId) >=3));
            case OBJECTIVEX:
                /** Conquer 24 territories, this objective is set when the player cannot achieve their 
                 * objective due to another player's elimination */
                /* Has the player conquered 24 territories? */
                return (nOfTerritoriesOccupied >= territoriesTwentyFour);
            /** If it doesn't match any of the above objectives, that means the player's objective 
             * was not achieved */   
            default:
                return false;
        }
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
            if (ownsContinent(playerId, continent.getName())) {
                nOfContinents++;
            }
        }
        return nOfContinents;
    }

    /* Checks whether a player owns a certain continent */
    boolean ownsContinent(String playerId, String Continent) {
        return map.getContinentOwner(Continent).equals(Optional.of(playerId));
    }
}
