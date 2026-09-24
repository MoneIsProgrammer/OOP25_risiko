package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.view.cards.CardView;

/**
 * This phase allows the current player to play cards before 
 * the reinforcement phase.
 * Before entering this phase, check whether the player has 
 * at least three cards they can play.
 */
public class PlaycardsPhase {
    private static final int MIN_CARDS = 3;

    private final PlayerTurn turn;

    /* Receive current player from player turn */
    private Player player;
    //private PlayerStrategy strategy;
    //private HumanStrategy humanStrategy;
    private CardView cView;
    private boolean isCompleted;

    /**
     * Default constructor.
     * 
     * @param turn the current turn
     */
    PlaycardsPhase(final PlayerTurn turn) {
        this.turn = turn;
    }

    /**
     * Checks if phase can be started.
     */
    public void phaseStart() {
        /*
         * Before allowing the player to play cards, 
         * checks whether the player has at least three 
         * cards to play
         */
        if (player.getHand().size() >= MIN_CARDS) {
            playCard();
        }

        isCompleted = true;
    }

    /**
     * @return true if phase is completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /** 
     * Based on whether a player is human or ai, calls different methods to allow player to play cards.
     */
    void playCard() {
        player = turn.getCurrentPlayer();
        if (player.isHuman()) {
            final var strategy = player.getStrategy();
            final var humanStrategy = (HumanStrategy) strategy;
            cView.askComboToPlay(player);
            humanStrategy.cardsToPlay(cView.getCombo());
        } else {
            player.playCard();
        }
    }
}
