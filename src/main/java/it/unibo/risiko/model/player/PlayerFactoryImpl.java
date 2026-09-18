package it.unibo.risiko.model.player;

import java.util.Objects;

import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.player.PlayerRequest.PlayerStrategyRequest;
import it.unibo.risiko.model.player.strategy.HumanStrategyImpl;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;
import it.unibo.risiko.model.player.strategy.ai.AggressiveStrategy;
import it.unibo.risiko.model.player.strategy.ai.DefensiveStrategy;
import it.unibo.risiko.model.player.strategy.ai.RandomStrategy;

/**
 * Implementation of {@link PlayerFactory}.
 */
public final class PlayerFactoryImpl implements PlayerFactory {

    private static final int TROOPS_PER_MISSING_PLAYER = 5;
    private static final int MIN_TROOPS = 20;
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_PLAYERS = 3;

    @Override
    public Player generatePlayer(final PlayerRequest playerRequest, final Roster roster, final GameMap map, final int players) {
        Objects.requireNonNull(playerRequest);
        return new PlayerImpl(playerRequest.color(), 
        playerRequest.name(), 
        createStrategy(playerRequest.ai(), roster, map), 
        startingForces(players));
    }

    /**
     * Generate player but the randoms all have same seed, useful for debug purposes.
     * 
     * @param playerRequest same as default
     * @param roster same as default
     * @param map same as default
     * @param players same as default
     * @return same as default
     */
    public Player generateSeededRandom(
        final PlayerRequest playerRequest,
        final Roster roster, 
        final GameMap map, 
        final int players
    ) {
        if (playerRequest.ai() == PlayerStrategyRequest.RANDOM) {
            return new PlayerImpl(playerRequest.color(), 
            playerRequest.name(), 
            new RandomStrategy(roster, map, 10), 
            startingForces(players));
        }
        return generatePlayer(playerRequest, roster, map, players);
    }

    private int startingForces(final int n) {
        if (n < MIN_PLAYERS || n > MAX_PLAYERS) {
            throw new IllegalArgumentException("players are not between 3 and 6 inclusive");
        }
        return MIN_TROOPS + (TROOPS_PER_MISSING_PLAYER * (MAX_PLAYERS - n));
    }

    private PlayerStrategy createStrategy(final PlayerStrategyRequest request, final Roster roster, final GameMap map) {
        Objects.requireNonNull(request);
        if (request.equals(PlayerStrategyRequest.AGGRESSIVE)) {
            return new AggressiveStrategy(roster, map);
        }
        if (request.equals(PlayerStrategyRequest.DEFENSIVE)) {
            return new DefensiveStrategy(roster, map);
        }
        if (request.equals(PlayerStrategyRequest.RANDOM)) {
            return new RandomStrategy(roster, map);
        }
        if (request.equals(PlayerStrategyRequest.HUMAN)) {
            return new HumanStrategyImpl(roster, map);
        }
        throw new IllegalArgumentException("The ai requested does not exists");
    }
}
