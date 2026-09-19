package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.deck.CardBonus;
import it.unibo.risiko.model.player.Player;

/**
 * This phase allows the current player to play cards before 
 * the reinforcement phase
 * PlayCards
 */
public class PlaycardsPhase {
    private Player player;
    private CardBonus bonus;
    private final PlayerTurn phase;
    int armies;

    PlaycardsPhase(Player player, CardBonus bonus, PlayerTurn phase) {
        this.player = player;
        this.bonus = bonus;
        this.phase = phase;
    }

    /*FIXME: The player chooses the cards he wants to play, do I 
    implement that part here or has it already been implemented? */

    public int getTotaltArmies() {
        // FIXME: add parameter setofcards i.e. cards that the player wants to play
        return armies = this.bonus.bonusReinforcements(player.getId(), null);
    }

    void playcardFinished() {
        phase.advancePhase();
    }

}
