package it.unibo.risiko.model.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This is a super class. It forms the basis of all decks including 
 * territories deck and objectives deck.
 * Attributes: deck
 * Methods: clear(), add(card), remove(card), shuffle(),
 * dealCard(), dealObjectiveCard(), populateTerritoryDeck(),
 * addJolly(), populateObjectiveDeck()
 */
public class Deck {
    /**
     * deck is a list of all the cards inside the array list.
     */
    private final List<Card> cards;
    /**
     * In order to not create a new object everytime we need a random number,
     * we'll use this random number generator.
     */
    private final Random random = new Random();
    /**
     * cardsUsed is a variable used to keep track of the number of cards
     * that have been dealt from the territory deck.
     */
    private int cardsUsed;

    /**
     * objectiveCardsUsed keeps track of the number of objective cards 
     * that have been dealt from the objective deck.
     */
    private int objectiveCardsUsed;

    /** 
     * deckLength helps calculate the length of the territory deck, for 
     * when we need to deal cards.
     */
    private int deckLength;

    /** 
     * objectiveDeckLength helps calculate the length of the objectives deck, 
     * for when we need to deal objective cards.
     */
    private int objectiveDeckLength;

    /** 
     * can aka cannon, cav aka cavalry, inf aka infantry are variables used 
     * as counters for populating the territory deck.
     */
    private int can;
    private int cav;
    //private int inf; useless

    /**
     * Default constructor.
     */
    public Deck() {
        cards = new ArrayList<>();
    }

    /**
     * To clear the hand and the cards in deck.
     */
    public void clear() {
        cards.clear();
    }

    /**
     * To add a card to a deck.
     * 
     * @param card the card to add
     */
    public void add(final Card card) {
        cards.add(card);
    }

    /**
     * To remove a card from a hand.
     * 
     * @param card the card to remove
     */
    public void remove(final Card card) {
        cards.remove(card);
    }

    /**
     * dealCard is a method that deals a random card, if all 
     * the cards in the deck have already been dealt, it 
     * re-populates the deck.
     * The card that is dealt, gets removed from the deck 
     * using the remove() method. The reference of the object 
     * is removed and then eventually the memory allocated for 
     * the object is freed thanks to the garbage collector.
     * 
     * @return returns a random card from the deck
     */
    public Card dealCard() {
        /*
         * Everytime the dealCard method is called, it checks 
         * whether all the cards have been used by comparing 
         * the number of cards used to the length of the deck
         */
        if (cardsUsed == deckLength) {
            this.populateTerritoryDeck();
            this.addJolly();
            this.shuffle();
            cardsUsed = 0;
        }
        /*
         * index: Using the random variable from the Random class,
        it gets a random number between 0 and 42 (not included) */
        final int index = random.nextInt(cards.size());
        final Card card = this.cards.get(index);
        remove(card);
        cardsUsed++;
        return card;
    }

    /**
     * Everytime the dealObjectiveCard method is called, it check whether
     * all the cards have been used by comparing the number of cards used 
     * to the length of the deck.
     * 
     * @return the objective card
     */
    public Card dealObjectiveCard() {
        if (objectiveCardsUsed == objectiveDeckLength) {
            this.populateObjectiveDeck();
            this.shuffle();
            objectiveCardsUsed = 0;
        }
        /*
         * index: using the random variable from the Random class
         * it gets a random number between 0 and 14 (not included)
         */
        final int index = random.nextInt(cards.size());
        final Card card = this.cards.get(index);
        remove(card);
        objectiveCardsUsed++;
        return card;
    }

    /**
     * To shuffle the cards in a deck.
     */
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    /** 
     * Function that returns a random value from the enum CardTroops.
     * 
     * @return a cardTroop
     */
    CardTroops getRandomTroop() {
        /* Given that the deck needs to have 14 cards per troop, we'll
         * first return 14 cannon troops, then 14 cavalry troops, 
         * then 14 infantry troops
         */
        final int nOfTroop = 14;
        if (can < nOfTroop) {
            can++;
            return CardTroops.CANNONS;
        } else if (cav < nOfTroop) {
            cav++;
            return CardTroops.CAVALRY;
        } else {
            //inf++; useless
            return CardTroops.INFANTRY;
        }
    }

    /**
     * Populates the deck by creating 42 cards with 3 troops*14 territories i.e.
     * each troop has 14 territories associated
     */
    public void populateTerritoryDeck() {
        /*
         * territoriesForTroop helps evenly divide the number of territories per troop
        */
        //final int territoriesForTroop = CardTerritories.values().length / CardTroops.values().length; was used
        /* To populate, we're going to loop through all of our 
        troops and for each troop we'll add 14 territories */
        final var terr = CardTerritories.values();
        final var troop = CardTroops.values();
        for (int i = 0; i < terr.length; i++) {
            this.add(new Card(terr[i], troop[i % CardTroops.values().length]));
        }
        /* Now that the card has been populated
         * we'll update the length of the deck
         */
        deckLength = cards.size();
    }

    /**
     * Adds two jolly cards to our territories deck.
     */
    public void addJolly() {
        /* We'll add two jolly cards to the territories deck, they must have
         all three symbols (cannon, infantry and cavalry) */
        final Card cj1 = new Card();
        final Card cj2 = new Card();
        this.add(cj1);
        this.add(cj2);
        /* Now that more cards have been added, we'll update the length 
         * of the deck */
        deckLength = cards.size();
    }

    /**
     * Populate the objectives deck.
     */
    public void populateObjectiveDeck() {
        for (final Objective objective: Objective.values()) {
            final Card card = new Card(objective);
            this.add(card);
        }
        objectiveDeckLength = cards.size();
    }
}
