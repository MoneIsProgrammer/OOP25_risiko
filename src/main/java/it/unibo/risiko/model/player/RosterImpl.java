package it.unibo.risiko.model.player;

import java.util.List;
import java.util.Optional;

import it.unibo.risiko.model.common.Registry;
import it.unibo.risiko.model.map.GameMap;

/**
 * Implementation of {@link Roster}.
 */
public final class RosterImpl implements Roster {
    private static final int MAX_PLAYERS = 6;
    private final Registry<Player> roster;

    /**
     * Default constructor.
     * 
     * @param players list of player to add to the roster
     * @param map the map where players will play
     */
    public RosterImpl(final List<PlayerRequest> players, final GameMap map) {
        this.roster = new Registry<>("players");
        final PlayerFactory factory = new PlayerFactoryImpl();
        if (players.size() > MAX_PLAYERS || players.size() < 3) {
            throw new IllegalArgumentException("Number of players is not between 3 and 6");
        }
        if (players.stream().map(PlayerRequest::color).distinct().count() != players.size()) {
            throw new IllegalArgumentException("Duplicate Color");
        }
        for (final PlayerRequest request : players) {
            this.roster.add(factory.generatePlayer(request, this, map, players.size()));
        }
    }

    @Override
    public Player getPlayer(final String playerId) {
        return this.roster.get(playerId);
    }

    @Override
    public Optional<Player> getPlayer(final RisikoColors color) {
        return this.roster.values().stream().filter(a -> a.getColor() == color).findFirst();
    }

    @Override
    public List<Player> getAllPlayers() {
        return List.copyOf(this.roster.values());
    }
}
