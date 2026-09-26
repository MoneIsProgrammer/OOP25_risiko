package it.unibo.risiko.model.event;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.view.GameScene;

/**
 * Informs the view to show Game Over alert.
 */
public class GameOverObserver {
    private GameScene scene;

    /** 
     * Method called by PlayerTurn only when a player wins.
     * 
     * @param winner the winner
     */
    public void onGameOver(final Player winner) {
        scene.showGameOver(winner);
    }
}
