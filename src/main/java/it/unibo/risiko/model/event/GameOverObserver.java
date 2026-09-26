package it.unibo.risiko.model.event;

import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.view.GameScene;

/**
 * Informs the view to show Game Over alert.
 */
public class GameOverObserver {
    private final GameScene scene;

    GameOverObserver(final GameScene scene) {
        this.scene = scene;
    }
    
    /* method called by PlayerTurn only when a player wins  */
    public void onGameOver(Player winner) {
        scene.showGameOver(winner);
    }
}
