package it.unibo.risiko.model.player.strategy.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;
import it.unibo.risiko.model.player.strategy.StrategyUtils;

/**
 * PlayerStrategy where the ai attacks whoever it can if it has enough troops.
 */
public final class AggressiveStrategy implements PlayerStrategy {

    private final Roster roster;
    private final GameMap map;

    /**
     * Default constructor of {@link AggressiveStrategy}.
     * 
     * @param roster The other players
     * @param map The playing map
     */
    public AggressiveStrategy(final Roster roster, final GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    public Optional<AttackEvent> getAttack(final Player owner) { // if it can attack it will
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        final var source = borders.stream()
        .filter(a -> a.getArmies() > 1)
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (source.isEmpty()) {
            return Optional.empty();
        }
        final var destination = source.get().getAdjacentIds().stream() // ok because souce is a border
            .map(map::getTerritory)
            .filter(a -> !playerTerritories.contains(a))
            .min(StrategyUtils.TERRITORY_COMPARATOR);
        if (destination.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new AttackEvent(owner, 
            this.roster.getPlayer(destination.get().getOwnerId().get()), 
            attackerStrenght(source.get()), 
            defenderStrenght(destination.get()), 
            source.get(), 
            destination.get()));
    }

    @Override
    // take the territory that isnt on the border with the most troops and moves all - 1 to the border with fewer troops.
    public Optional<MoveEvent> getMove(final Player owner) {
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        final var source = borders.stream()
        .map(Territory::getAdjacentIds)
        .flatMap(Set::stream) //flattens the stream
        .map(this.map::getTerritory)
        .filter(a -> a.getOwnerId().get().equals(owner.getId())) // only owned by player
        .filter(a -> !borders.contains(a)) //remove borders
        .filter(a -> a.getArmies() > 2) // 3 armies at least
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (source.isEmpty()) {
            //System.out.println("Move got no source");
            return Optional.empty();
        }
        final var destination = source.get().getAdjacentIds().stream() //weakest border territory adj to the strongest non border
        .map(this.map::getTerritory)
        .filter(borders::contains)
        .min(StrategyUtils.TERRITORY_COMPARATOR);
        if (destination.isEmpty()) {
            //System.out.println("Move got no dest");
            return Optional.empty();
        }
        return Optional.of(new MoveEvent(owner,
        source.get(),
        destination.get(),
        source.get().getArmies() - 2));
    }

    @Override
    public ReinforceEvent getReinforce(final Player owner, final int armies) { // TODO add card bonuses when ready
        final Map<Territory, Integer> reinforceMap = new HashMap<>();
        final var playerTerritories = map.getTerritoriesOf(owner.getId());
        final var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        for (final Territory territory : playerTerritories) {
            reinforceMap.put(territory, territory.getArmies());
        }
        for (int i = 0; i < armies; i++) {
            var min = reinforceMap.entrySet().stream().filter(a -> a.getValue() < 2).findAny();
            if (min.isEmpty()) {
                min = reinforceMap.entrySet().stream()
                    .filter(a -> borders.contains(a.getKey()))
                    .min((a, b) -> Integer.compare(a.getValue(), b.getValue()));
            }
            // sets the number of time a territory is to be reinforced with 1 troop
            reinforceMap.merge(min.get().getKey(), 1, Integer::sum);
        }
        reinforceMap.replaceAll((k, v) -> v - k.getArmies());
        reinforceMap.entrySet().removeIf(a -> a.getValue() == 0);
        return new ReinforceEvent(owner, reinforceMap);

    }

    private int attackerStrenght(final Territory territory) {
        final var str = territory.getArmies();
        if (str > 3) {
            return 3;
        } else {
            return str - 1;
        }
    }

    private int defenderStrenght(final Territory territory) {
        final var str = territory.getArmies();
        if (str > 3) {
            return 3;
        } else {
            return str;
        }
    }

    @Override
    public ReinforceEvent getSetup(final Player owner, final int startingForces) {
        return this.getReinforce(owner, startingForces);
    }

    @Override
    public Optional<CardEvent> playCards(final List<Card> hand, final Player owner) {
        return StrategyUtils.genericCardPlay(hand, owner, this.map);
    }

    @Override
    public MoveEvent getMoveAfterConquest(final String sourceID, final String destinationID, final Player owner) {
        final var source = this.map.getTerritory(sourceID);
        final var destination = this.map.getTerritory(destinationID);
        return new MoveEvent(owner, source, destination, source.getArmies() - 1);
    }
}
