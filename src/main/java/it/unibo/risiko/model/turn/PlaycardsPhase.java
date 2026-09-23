package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;
import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.view.cards.CardView;

/**
 * This phase allows the current player to play cards before 
 * the reinforcement phase
 * Before entering this phase, check whether the player has 
 * at least three cards they can play
 * PlayCards
 */
public class PlaycardsPhase{
    private final PlayerTurn turn;

    /* Receive current player from player turn */
    private Player player;
    private PlayerStrategy strategy;
    private HumanStrategy humanStrategy;
    private CardView cView;
    private boolean isCompleted = false;
    private final int MIN_CARDS = 3;

    public void phaseStart() {
        /**
         * Before allowing the player to play cards, 
         * checks whether the player has at least three 
         * cards to play
         */
        if (player.getHand().size() >= MIN_CARDS) {
            playCard();
        }

        isCompleted = true;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    PlaycardsPhase(final PlayerTurn turn) {
        this.turn = turn;
    }

    /** Based on whether a player is human or ai, calls different methods to 
     * allow player to play cards  */
    void playCard() {
        player = turn.getCurrentPlayer();
        if (player.isHuman()) {   
            strategy = player.getStrategy();
            humanStrategy = (HumanStrategy) strategy;
            cView.askComboToPlay(player);
            humanStrategy.cardsToPlay(cView.getCombo());
        } else {
            player.playCard();
        }
    }
}
