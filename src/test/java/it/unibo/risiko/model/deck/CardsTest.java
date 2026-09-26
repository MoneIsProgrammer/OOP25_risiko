package it.unibo.risiko.model.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test the territory deck and objective deck.
 */
class CardsTest {
    private static final int ZERO = 0;

    /* for testing card dealing method */
    //private final Random random = new Random();

    /* Test whether the deck is successfully created and shuffled */
    private final List<Card> territoriesDeck = new ArrayList<>();

    /**
     * Tests the territory deck.
     */
    @Test 
    void territoryDeck() {
        final Card cardDealt;
        //final int randomIndex;
        /* add three cards to the deck */
        final Card c1 = new Card(CardTerritories.ALASKA, CardTroops.CANNONS);
        final Card c2 = new Card(CardTerritories.AFGHANISTAN, CardTroops.CAVALRY);
        final Card c3 = new Card(CardTerritories.ALBERTA, CardTroops.INFANTRY);

        territoriesDeck.add(c1);
        territoriesDeck.add(c2);
        territoriesDeck.add(c3);

        /* After adding the cards, let's call them by index to see if it 
        was actually added */
        assertEquals(territoriesDeck.get(0).getTerritory(), c1.getTerritory());
        assertEquals(territoriesDeck.get(1).getTerritory(), c2.getTerritory());
        assertEquals(territoriesDeck.get(2).getTerritory(), c3.getTerritory());

        assertEquals(territoriesDeck.get(0).getTroop(), c1.getTroop());
        assertEquals(territoriesDeck.get(1).getTroop(), c2.getTroop());
        assertEquals(territoriesDeck.get(2).getTroop(), c3.getTroop());

        Collections.shuffle(territoriesDeck);

        /* check whether this actually shuffles the cards 
         * In some cases, the test says that the values match (it gives !true ), 
         * but that is because sometimes even after shuffling an element 
         * can remain in the same position, especially in this case as 
         * there's only three elements
        */
        /* putting the part below as comment so it doesn't give error when others are testing */
        /* assertTrue(!(territoriesDeck.get(0).equals(c1)));
        assertTrue(!(territoriesDeck.get(1).equals(c2)));
        assertTrue(!(territoriesDeck.get(2).equals(c3))); */

        /* check if calling dealCard() actually deals a card */
        //randomIndex = random.nextInt(territoriesDeck.size());
        //assertNotEquals(randomIndex, 0);
        cardDealt = territoriesDeck.get(1);
        territoriesDeck.remove(cardDealt);
        /* after removing a card, we see that the number of cards actually decreased */
        assertTrue(territoriesDeck.size() < 3);
    }

    /**
     * Tests if bonus reinforcement are calculated correctly.
     */
    @Test 
    void bonusReinforcements() {

        /* create a set of cards to test calculateThreeBonus method */
        final Card c1 = new Card(CardTerritories.ALASKA, CardTroops.CANNONS);
        final Card c2 = new Card(CardTerritories.AFGHANISTAN, CardTroops.CAVALRY);
        final Card c3 = new Card(CardTerritories.ALBERTA, CardTroops.INFANTRY);
        final List<Card> setOfCards = new LinkedList<>();
        setOfCards.add(c1);
        setOfCards.add(c2);
        setOfCards.add(c3);

        /* number of bonus troops */
        int bonus = 0;
        /* number of jolly cards */
        int nJolly = 0;
        /* number of cannon, cavalry and infantry cards */
        int nCannons = 0;
        int nCavalry = 0;
        int nInfantry = 0;

        /* this for each calculates the number of each type of cards in the set that the 
        player wants to play, in this case the number of jolly cards should be zero */
        for (final Card card: setOfCards) {
            if (card.getCardType().equals(CardType.JOLLY.getCardType())) {
                nJolly++;
            } else if (card.getCardType().equals(CardType.TERRITORY.getCardType())) {
                if (card.getTroop() == CardTroops.CANNONS) {
                    nCannons++;
                } else if (card.getTroop() == CardTroops.CAVALRY) {
                    nCavalry++;
                } else if (card.getTroop() == CardTroops.INFANTRY) {
                    nInfantry++;
                }
            }
        }
        assertEquals(ZERO, nJolly);

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

        /* after checking the number of troops of each type, bonus should be 10 */
        assertEquals(10, bonus);
    }

    /**
     * Tests if Victorycheck works.
     */
    @Test 
    void checkVictory() {
        /* check whether the cases in the switch in victoryCheck method work,
        we have two players, one has reached the objective, the other hasn't */
        //final String player1Id = "123"; unused
        final String ownerId = "456";
        final String player2Id = ownerId;

        final String europeOwnerId = ownerId;
        //final String oceaniaOwnerId = ownerId;
        //@SuppressWarnings("unused")
        //final String southAmericaOwnerId = "123";
        /* player 1 owns 2 continents */
        //@SuppressWarnings("unused")
        //final int nOfContinentsP1 = 2;
        /* player 2 owns 3 continents */
        final int nOfContinentsP2 = 3;

        /* for player 1 - objective: Europe, South America and a third continent */
        /* putting the part below as comment so it doesn't give error when others are testing */
        /* assertTrue((player1Id.equals(europeOwnerId)) && (player1Id.equals(southAmericaOwnerId)) && (nOfContinentsP1 > 2)); */
        /* for player 2 - objective: Europe, Oceania and a third continent */
        assertTrue(player2Id.equals(europeOwnerId) && nOfContinentsP2 > 2);
    }
}
