package it.unibo.risiko.model.turn;

import it.unibo.risiko.controller.PlayerTurn;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.strategy.HumanStrategy;

/**
 * This phase allows the current player to attack.
 * 
 */
public class AttackPhase {

    private final PlayerTurn turn;
    private final GameMap map;
    private int initialTerritories;
    private boolean isCompleted;

    AttackPhase(final PlayerTurn turn, final GameMap map) {
        this.turn = turn;
        this.map = map;
    }

    /**
     * checks if the phase can be started.
     */
    public void phaseStart() {
        canAttack();

        isCompleted = true;
    }

    /**
     * @return true if it can be completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Based on whether a player is ai or human, allows player to attack other 
     * player's territories.
     */
    void canAttack() {
        final var player = turn.getCurrentPlayer();
        initialTerritories = map.getTerritoriesOf(player.getId()).size();
        if (!player.isHuman()) {
            player.attack();
        } else {
            final var strategy = player.getStrategy();
            final var humanStrategy = (HumanStrategy) strategy;
            if (humanStrategy.canCreateAttack()) {
                player.attack();
            }
        }
    }

    /**
     * This will return true if the player has conquered at least 
     * one territory
     * Its used during MovePhase, if player wants to draw a card
     * @return true if the current amount of territories the player 
     * has is more than initial territories
     */
    public boolean canDraw() {
        final var player = turn.getCurrentPlayer();

        return ((map.getTerritoriesOf(player.getId()).size()) > initialTerritories);
    }
}
