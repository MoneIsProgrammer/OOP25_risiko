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
public class AggressiveStrategy implements PlayerStrategy {

    private final Roster roster;
    private final GameMap map;

    public AggressiveStrategy(Roster roster, GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    public Optional<AttackEvent> getAttack(Player owner) { // if it can attack it will
        var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        var source = borders.stream()
        .filter(a -> a.getArmies() > 1)
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (source.isEmpty()) {
            return Optional.empty();
        }
        var destination = source.get().getAdjacentIds().stream() // ok because souce is a border
            .map(map::getTerritory)
            .filter(a -> !playerTerritories.contains(a))
            .min(StrategyUtils.TERRITORY_COMPARATOR);
        if (destination.isEmpty()) {
            return  Optional.empty();
        }
        return Optional.of(new AttackEvent(owner, 
            this.roster.getPlayer(destination.get().getOwnerId().get()), 
            attackerStrenght(source.get()), 
            defenderStrenght(destination.get()), 
            source.get(), 
            destination.get()));
    }

    @Override
    public Optional<MoveEvent> getMove(Player owner) { // take the territory that isnt on the border with the most troops and moves all - 1 to the border with fewer troops.
        var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        var source = borders.stream()
        .map(a -> a.getAdjacentIds())
        .flatMap(Set::stream) //flattens the stream
        .map(this.map::getTerritory)
        .filter(a -> a.getOwnerId().get().equals(owner.getId())) // only owned by player
        .filter(a -> !borders.contains(a)) //remove borders
        .filter(a -> a.getArmies() > 2) // 3 armies at least
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (source.isEmpty()) {
            System.out.println("Move got no source");
            return Optional.empty();
        }
        var destination = source.get().getAdjacentIds().stream() //weakest border territory adj to the strongest non border
        .map(this.map::getTerritory)
        .filter(borders::contains)
        .min(StrategyUtils.TERRITORY_COMPARATOR);
        if (destination.isEmpty()) {
            System.out.println("Move got no dest");
            return Optional.empty();
        }
        return Optional.of(new MoveEvent(owner,
        source.get(),
        destination.get(),
        source.get().getArmies() - 1));
    }

    @Override
    public ReinforceEvent getReinforce(Player owner, int armies) { // TODO add card bonuses when ready
        Map<Territory,Integer> reinforceMap = new HashMap<>();
        var playerTerritories = map.getTerritoriesOf(owner.getId());
        var reinforcements = Math.floor(playerTerritories.size() / 3); // arrotondamento per difetto
        reinforcements += map.getContinentBonus(owner.getId());
        var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        for (int i = 0; i < reinforcements; i++) {
            var min = playerTerritories.stream().filter(a -> a.getArmies() < 2).findAny();
            if (min.isEmpty()) {
                min = borders.stream().min(StrategyUtils.TERRITORY_COMPARATOR);
            }
            reinforceMap.merge(min.get(), 1,Integer::sum); // sets the number of time a territory is to be reinforced with 1 troop
        }
        return new ReinforceEvent(owner, reinforceMap);

    }

    private int attackerStrenght(Territory territory) {
        var str = territory.getArmies();
        if (str > 3) {
            return 3;
        } else {
            return str - 1;
        }
    }

    private int defenderStrenght(Territory territory) {
        var str = territory.getArmies();
        if (str > 3) {
            return 3;
        }
        else {
            return str;
        }
    }

    @Override
    public ReinforceEvent getSetup(Player owner, int startingForces) {
        return this.getReinforce(owner, startingForces);
    }

    @Override
    public Optional<CardEvent> playCards(List<Card> hand, Player owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playCards'");
    }
}
