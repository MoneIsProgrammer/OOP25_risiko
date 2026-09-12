package it.unibo.risiko.model.player;

import java.util.Objects;

/**
 * Used as DTO between view and model to generate players.
 * 
 * @param name name of the player
 * @param ai type of ai player will use
 * @param color color of the player, unique is preferred
 */
public record PlayerRequest(String name, PlayerStrategyRequest ai, RisikoColors color) {
    /**
     * Type of ai this player will use.
     */
    public static final class PlayerStrategyRequest {
        /**
         * Costant to be used to define the type of strategy the player will use.
         */
        public static final PlayerStrategyRequest AGGRESSIVE = new PlayerStrategyRequest("aggressive");

        /**
         * Costant to be used to define the type of strategy the player will use.
         */
        public static final PlayerStrategyRequest DEFENSIVE = new PlayerStrategyRequest("defensive");

        /**
         * Costant to be used to define the type of strategy the player will use.
         */
        public static final PlayerStrategyRequest RANDOM = new PlayerStrategyRequest("random");

        /**
         * Costant to be used to define the type of strategy the player will use.
         */
        public static final PlayerStrategyRequest HUMAN = new PlayerStrategyRequest("human");
        private final String ai;

        private PlayerStrategyRequest(final String ai) {
            this.ai = ai;
        }

        @Override
        public String toString() {
            return this.ai;
        }

        @Override
        public boolean equals(final Object object) {
            if (object instanceof PlayerStrategyRequest) {
                final PlayerStrategyRequest p = (PlayerStrategyRequest) object;
                return this.ai.equals(p.ai);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.ai);
        }
    }
}
