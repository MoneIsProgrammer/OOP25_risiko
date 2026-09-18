package it.unibo.risiko.model.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This is a super class. It forms the basis of all decks including 
 * territories deck and objectives deck
 * Attributes: cards
 * Methods: clear(), add(card), remove(card), shuffle(),
 * dealCard(), dealObjectiveCard(), populateTerritoryDeck(),
 * addJolly(), populateObjectiveDeck()
 */
public class Deck {
    /**
     * deck is a list of all the cards inside the array list
     */
    private ArrayList<Card> deck;
    /**
     * In order to not create a new object everytime we need a random number,
     * we'll use this random number generator
     */
    private final Random random = new Random();
    /**
     * cardsUsed is a variable used to keep track of the number of cards
     * that have been dealt from the territory deck
     */
    private int cardsUsed = 0;

    /**
     * objectiveCardsUsed keeps track of the number of objective cards 
     * that have been dealt from the objective deck
     */
    private int objectiveCardsUsed = 0;

    /** deckLength helps calculate the length of the territory deck, for 
     * when we need to deal cards
     */
    private int deckLength = 0;

    /** 
     * objectiveDeckLength helps calculate the length of the objectives deck, 
     * for when we need to deal objective cards
     */
    private int objectiveDeckLength = 0;

    /** can aka cannon, cav aka cavalry, inf aka infantry are variables used 
     * as counters for populating the territory deck
     */
    int can = 0, cav = 0, inf = 0;

    /* Constructor */
    public Deck() {
        deck = new ArrayList<Card>();
    }

    /*  To clear the hand and the cards in deck */
    public void clear() {
        deck.clear();
    }

    /* To add a card to a deck */
    public void add(Card card) {
        deck.add(card);
    }

    /* To remove a card from a hand */
    public void remove(Card card) {
        deck.remove(card);
    }

    /**
     * dealCard is a method that deals a random card, if all 
     * the cards in the deck have already been dealt, it 
     * re-populates the deck.
     * @return returns a random card from the deck
     * The card that is dealt, gets removed from the deck 
     * using the remove() method. The reference of the object 
     * is removed and then eventually the memory allocated for 
     * the object is freed thanks to the garbage collector.
     */
    public Card dealCard() {
        /**
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
        /**
         * index: Using the random variable from the Random class,
        it gets a random number between 0 and 42 (not included) */
        int index = random.nextInt(deck.size());
        Card card = this.deck.get(index);
        remove(card);
        cardsUsed++;
        return card;
    }

    /**
     * Everytime the dealObjectiveCard method is called, it check whether
     * all the cards have been used by comparing the number of cards used 
     * to the length of the deck
     */

    /* To shuffle the cards in the territory deck */
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    /* Function that returns a random value from the enum CardTroops */
    CardTroops getRandomTroop() {
        if (can < 14) {
            can++;
            return CardTroops.CANNONS;
        } else if (cav < 14) {
            cav++;
            return CardTroops.CAVALRY;
        } else {
            inf++;
            return CardTroops.INFANTRY;
        }
    }

    /* Populates the deck by creating 42 cards with 3 troops*14 territories i.e.
    each troop has 14 territories associated */
    public void populateTerritoryDeck() {
        /**
         * territoriesForTroop helps evenly divide the number of territories per troop
        */
        int territoriesForTroop = CardTerritories.values().length / CardTroops.values().length;
        /* To populate, we're going to loop through all of our 
        troops and for each troop we'll add 14 territories */
        for (CardTroops troopsName: CardTroops.values()) {
            for (int i = 0; i < territoriesForTroop; i++) {
                Card card = new Card(CardTerritories.values()[i++], troopsName);
                /* Here "this" refers to each individual deck we create */
                this.add(card);
            }
        }
        /** Now that the card has been populated
         * we'll update the length of the deck
         */
        deckLength = deck.size();
    }

    /* Adds two jolly cards to our territories deck */
    public void addJolly() {
        /* We'll add two jolly cards to the territories deck, they must have
         all three symbols (cannon, infantry and cavalry) */
        Card cj1 = new Card();
        Card cj2 = new Card();
        this.add(cj1);
        this.add(cj2);
        /** Now that more cards have been added, we'll update the length 
         * of the deck */
        deckLength = deck.size();
    }

    /* Populate the objectives deck */
    public void populateObjectiveDeck() {
        for (CardObjectives objectiveDescription: CardObjectives.values()) {
            Card card = new Card(objectiveDescription);
            this.add(card);
        }
    }
}
