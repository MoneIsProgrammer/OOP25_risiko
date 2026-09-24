package it.unibo.risiko.model.deck;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import java.util.List;
import java.util.Set;

/**
 * CardBonus contains the method:
 * bonusReinforcements, returns the number of troops a player can 
 * deploy as reinforcements.
 */
public class CardBonus {

    /* GameMap methods cannot be directly used within the class,
     * therefore, we add the following private field and constructor 
     * to use methods such as getTerritoriesOf(playerId);
     */
    private final GameMap map;

    /**
     * Default constructor.
     * 
     * @param map the map used in game for context
     */
    public CardBonus(final GameMap map) {
        this.map = map;
    }

    /** 
     * This method calculates the number of troops a player can deploy 
     * as reinforcements.
     * 
     * @param playerId id representing the player
     * @param setOfCards the cards that will be calculated for reinforcement
     * 
     * @return the total reinforcements
     */
    public int bonusReinforcements(final String playerId, final List<Card> setOfCards) {

        final int nOfTerritoriesOccupied = this.map.getTerritoriesOf(playerId).size();
        int troopsDeployable = nOfTerritoriesOccupied / 3;
        //Calculate the number of troops that the player can deploy
        /* 1. The player occupies x territories, so they have a right to x:3 territories
          (for example: nOfTerritoriesOccupied = 14, troopsDeployable = 14/3 = 4) ROUNDING
         DOWN */
        /* 2. If the player occupies all the territories within one or more continents,
         * each turn they have a right to a number of extra troops
         * This has been directly implemented within the GameMapImpl class, see
         * getContinentBonus method */
        /* 3. During the reinforcement phase, the player has the right to play a 
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
        troopsDeployable = troopsDeployable + calculateThreeBonus(setOfCards, playerId);
        /* needs to be a return method that returns the number of troops a player can deploy */
        return troopsDeployable;
    }

    /**
     * Calculates the bonus of the 3 cards passed.
     * 
     * @param setOfCards the cards to be evaluated
     * @param playerId id representing the player
     * @return the bonus from the cards
     */
    public int calculateThreeBonus(final List<Card> setOfCards, final String playerId) {
        /* Variable to store the number of bonus troops */
        int bonus = 0;
        /*
         * Calculate the number of jolly cards in the set
         */
        int nJolly = 0;
        for (final Card card : setOfCards) {
            if (card.getCardType().equals(CardType.JOLLY.getCardType())) {
                nJolly++;
            }
        }
        int nCannons = 0;
        int nCavalry = 0;
        int nInfantry = 0;
        for (final Card card : setOfCards) {
            if (card.getCardType().equals(CardType.TERRITORY.getCardType())) {
                if (card.getTroop() == CardTroops.CANNONS) {
                    nCannons++;
                } else if (card.getTroop() == CardTroops.CAVALRY) {
                    nCavalry++;
                } else if (card.getTroop() == CardTroops.INFANTRY) {
                    nInfantry++;
                }
            }
        }

        /* If the number of jolly cards is more than one, return 0 */
        if (nJolly > 1) {
            return 0;
        } else if (nJolly == 1) {
            /* If the number of jolly cards is 1, check whether the other two 
             * cards are of the same troop */
            if (nCannons == 2 || nCavalry == 2 || nInfantry == 2) {
                final int withJollyBonus = 12;
                bonus += withJollyBonus;
            }
        } else if (nJolly == 0) {
            /* No jolly cards, which means:
             * If all three cards are of the same kind, I'll check whether:
             * They're all cannon and 4 extra troops can be deployed;
             * They're all infantry and 6 extra troops can be deployed;
             * They're all cavalry and 8 extra troops can be deployed;
             * If they're each a different type of troop, 10 extra troops 
             * can be deployed;
             */
            if (nCannons == 3) {
                final int cannonBonus = 4;
                bonus += cannonBonus;
            } else if (nInfantry == 3) {
                final int infantryBonus = 6;
                bonus += infantryBonus;
            } else if (nCavalry == 3) {
                final int cavarlyBonus = 8;
                bonus += cavarlyBonus;
            } else if (nCannons == 1 && nInfantry == 1 && nCavalry == 1) {
                final int trisBonus = 10;
                bonus += trisBonus;
            }
        }
        /*Then, we'll check if the territory bonus applies, by calling the 
         * checkTerritory method
         */
        if (bonus > 0) {
            bonus = bonus + checkTerritory(playerId, setOfCards);
        }
        return bonus;
    }
    /*
     * Checks whether in the special cases, the player occupies a territory between those 
     * represented on the cards, as each territory present on the cards and occupied by the 
     * player, grants two extra troops.
     * 
     * 
     */

    private int checkTerritory(final String playerId, final List<Card> setOfCards) {
        int reinforcements = 0;
        /* Gets a collection of all the territories occupied by the player */
        final Set<Territory> territoriesOccupied = this.map.getTerritoriesOf(playerId);
        /*
         * Iterates through the set of cards that the player wants to play and 
         * through the territories occupied by the player to find a match,
         * returns true if it finds a match. If it exits the loops without finding 
         * a match, then false is returned.
         */
        for (final Card card: setOfCards) {
            for (final Territory territory: territoriesOccupied) {
                if ((card.getTerritory().getTerritoryId()).equals(territory.getId())) {
                    reinforcements = reinforcements + 2;
                }
            }
        }
        return reinforcements;
    }
}
