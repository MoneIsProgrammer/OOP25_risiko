package it.unibo.risiko.model.player;

import java.util.List;
import java.util.Optional;

import it.unibo.risiko.model.common.Registry;
import it.unibo.risiko.model.map.GameMap;

public class RosterImpl implements Roster {
    private final Registry<Player> roster;
    
    public RosterImpl(List<PlayerRequest> players, GameMap map){
        this.roster = new Registry<Player>("players");
        PlayerFactory factory = new PlayerFactoryImpl();
        for (PlayerRequest request : players) {
            this.roster.add(factory.generatePlayer(request, this, map, players.size()));
        }
    }
    
    @Override
    public Player getPlayer(String playerId) {
        return this.roster.get(playerId);
    }

    @Override
    public Optional<Player> getPlayer(RisikoColors color) {
        return  this.roster.values().stream().filter(a -> a.getColor() == color).findFirst();
    }

    @Override
    public List<Player> getAllPlayers() {
        return List.copyOf(this.roster.values());
    }
}
