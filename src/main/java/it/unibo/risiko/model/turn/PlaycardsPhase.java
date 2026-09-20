package it.unibo.risiko.model.turn;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.controller.PhaseController;

/**
 * This phase allows the current player to play cards before 
 * the reinforcement phase
 * Before entering this phase, check whether the player has 
 * at least three cards they can play
 * PlayCards
 */
public class PlaycardsPhase implements PhaseController{
    // TODO: this class needs to receive current player
    private final Player player;

    public void phaseStart() {
        playCard();
    }

    PlaycardsPhase(final Player player) {
        this.player = player;
    }

    // FIXME: does strategy.playCards automatically do everything
    void playCard() {
        player.playCard();
        //strategy.playCards(player.getHand(), player);
    }
}
