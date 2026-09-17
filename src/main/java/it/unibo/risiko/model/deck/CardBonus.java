package it.unibo.risiko.model.deck;

import it.unibo.risiko.model.map.*;
import java.util.ArrayList;
import java.util.Set;

    /**
     * CardStrategy contains two methods:
     * cardsValid, checks whether a certain combination of cards can be played
     * bonusReinforcements, returns the number of troops a player can deploy as reinforcements
     */
public class CardBonus {
    String troopname;   
    
    /** GameMap methods cannot be directly used within the class,
     * therefore, we add the following private field and constructor 
     * to use methods such as getTerritoriesOf(playerId);
     */
    private final GameMap map;

    public CardBonus(final GameMap map) {
        this.map = map;
    }

    /** cardsValid checks whether a set of cards can be played, we need the setOfCards 
     * and the list of territories occupied by the player
     * It returns true if the set can be played, false if it can't be played
    */
   // TODO: In che senso sia valida?
    public boolean cardsValid(ArrayList<Card> setOfCards, String playerId) {
        boolean torf = false;
        //Set<Territory> territoriesOccupied = this.map.getTerritoriesOf(playerId);
        return torf;
    }
    //it.unibo.risiko.model.map.GameMapImpl.getTerritoriesOf

    /** This method calculates the number of troops a player can deploy 
     * as reinforcements 
     * In input, this method: gets the number of territories occupied by 
     * the player; the cards that the player wants to play */
    int bonusReinforcements(String playerId, ArrayList<Card> setOfCards) {
        int nOfTerritoriesOccupied = this.map.getTerritoriesOf(playerId).size();
        int troopsDeploy = 0;
        /** First, it checks whether the set of cards can be played 
         * If it can't be played it sends an error message.
         * If it can be played, calculate the number of troops that the 
         * player can deploy
        */
        if (!(cardsValid(setOfCards, playerId))) {
            // TODO: cosa fare nel caso in cui la combinazione di carte non è valida
            return 0;
        } else {
            /** 1. The player occupies x territories, so they have a right to x:3 territories
             * (for example: nOfTerritoriesOccupied = 14, troopsDeploy = 14/3 = 4) ROUNDING
             * DOWN
             */
            troopsDeploy = nOfTerritoriesOccupied/3;
            /** 2. If the player occupies all the territories within one or more continents,
             * each turn they have a right to a number of extra troops
             * This has been directly implemented within the GameMapImpl class, see
             * getContinentBonus method
             */
            /** 3. During the reinforcement phase, the player has the right to play a 
             * three-of-a-kind cards, thanks to which they can add troops. The valid 
             * combinations are: 
             * 3 cannons: 4 troops
             * 3 infantries: 6 troops
             * 3 cavalries: 8 troops
             * 1 cannon, 1 infantry and 1 cavalry: 10 troops
             * 1 jolly and two of the same cards: 12 troops 
             * Moreover, if the player occupies any of the territories represented on 
             * the cards, they get 2 troops for each of these cards */
            /* For calculating the bonus for the three of a kind cards, we'll call the 
            calculateThreeBonus method */
            troopsDeploy = troopsDeploy + calculateThreeBonus(setOfCards, playerId);
        }
        /* needs to be a return method that returns the number of troops a player can deploy */
        return troopsDeploy;
    }

    public int calculateThreeBonus (ArrayList<Card> setOfCards, String playerId) {
        /* Variable to store the number of bonus troops */
        int bonus = 0;
        /**
         * Calculate the number of jolly cards in the set
         */
        long nJolly = setOfCards.stream().filter(c -> "JOLLY".equals(c.getCardType())).count();
        long nCannons = setOfCards.stream().filter(c -> "CANNONS".equals(c.getTroop())).count();
        long nCavalry = setOfCards.stream().filter(c -> "CAVALRY".equals(c.getTroop())).count();
        long nInfantry = setOfCards.stream().filter(c -> "INFANTRY".equals(c.getTroop())).count();

        /* If the number of jolly cards is more than one, return 0 */
        if (nJolly > 1) {
            return 0;
        } else if (nJolly == 1) {
            /** If the number of jolly cards is 1, check whether the other two 
             * cards are of the same troop */
            if (nCannons == 2 || nCavalry == 2 || nInfantry == 2) {
                bonus += 12;
            }
        } else if (nJolly == 0) {
            /** No jolly cards, which means:
             * If all three cards are of the same kind, I'll check whether:
             * They're all cannon and 4 extra troops can be deployed;
             * They're all infantry and 6 extra troops can be deployed;
             * They're all cavalry and 8 extra troops can be deployed;
             * If they're each a different type of troop, 10 extra troops 
             * can be deployed;
             */
            if (nCannons == 3) {
                bonus += 4;
            } else if (nInfantry == 3) {
                bonus += 6;
            } else if (nCavalry == 3) {
                bonus += 8;
            } else if ((nCannons == 1) && (nInfantry == 1) && (nCavalry == 1)) {
                bonus += 10;
            }
        }
        /** Then, we'll check if the territory bonus applies, by calling the 
         * checkTerritory method
         */
        if (bonus > 0) {
            bonus = bonus + checkTerritory(playerId, setOfCards);
        }
        return bonus;
    }
    /**
     * Checks whether in the special cases, the player occupies a territory between those 
     * represented on the cards, as each territory present on the cards and occupied by the 
     * player, grants two extra troops
     */
    int checkTerritory(String playerId, ArrayList<Card> setOfCards) {
        int reinforcements = 0;
        /* Gets a collection of all the territories occupied by the player */
        Set<Territory> territoriesOccupied = this.map.getTerritoriesOf(playerId);
        /**
         * Iterates through the set of cards that the player wants to play and 
         * through the territories occupied by the player to find a match,
         * returns true if it finds a match. If it exits the loops without finding 
         * a match, then false is returned.
         */
        for (Card card: setOfCards) {
            for (Territory territory: territoriesOccupied) {
                if (card.getTerritoryName().equals(territory.getName())) {
                    reinforcements = reinforcements + 2;
                }
            }
        }
        return reinforcements;
    }
}
