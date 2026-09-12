package it.unibo.risiko.model.player.strategy.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
 * PlayerStrategy where the ai prefers to bolster its defenses rather than attack recklessly.
 */
public class DefensiveStrategy implements PlayerStrategy {


    private static final int MAX_ATK_STR = 3;
    private final Roster roster;
    private final GameMap map;

    public DefensiveStrategy(Roster roster, GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    public Optional<AttackEvent> getAttack(Player owner) { //attacks only when has more than _3 troops and his troops > his target troops
        var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        var validAttacks = borders.stream().filter(a -> a.getArmies() > MAX_ATK_STR).collect(Collectors.toMap(a -> a, createAdjEnemySet(owner.getId()))); // this.map of territories and their adjacent enemies
        validAttacks.entrySet().removeIf(a -> a.getValue().isEmpty());// prune entries where the attacker has no valid targets
        if (validAttacks.size() == 0) {
            return  Optional.empty();
        }
        var strongestAttacker = validAttacks.keySet().stream().max(StrategyUtils.TERRITORY_COMPARATOR).get();
        var weakestVictim = validAttacks.get(strongestAttacker).stream().min(StrategyUtils.TERRITORY_COMPARATOR).get();
        return Optional.of(new AttackEvent(owner,
            this.roster.getPlayer(weakestVictim.getOwnerId().get()),
            strongestAttacker.getArmies() > MAX_ATK_STR ? MAX_ATK_STR : strongestAttacker.getArmies() - 1, // this will always result in max armies, useful if the attack policy changes
            weakestVictim.getArmies() > MAX_ATK_STR ? MAX_ATK_STR : weakestVictim.getArmies(), // to simplify defenders always defend with all their armies (MAX _3 min 1)
            strongestAttacker,
            weakestVictim
        ));
    }

    @Override
    public Optional<MoveEvent> getMove(Player owner) {//println only dirty debugging purposes
        var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        var border = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        var weakestBorder = border.stream().filter(a -> StrategyUtils.notIsolated(a, map)).min(StrategyUtils.TERRITORY_COMPARATOR);
        if (weakestBorder.isEmpty()) {
            System.out.println("no weakest border");
            return Optional.empty();
        }
        var strongestAdj = weakestBorder.get().getAdjacentIds().stream()
        .map(this.map::getTerritory)
        .filter(a -> !border.contains(a))
        .filter(a -> a.getOwnerId().get().equals(owner.getId()))
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (strongestAdj.isEmpty()) {
            System.out.println("no Strongest adj");
            return Optional.empty();
        }
        return Optional.of(new MoveEvent(owner,
            strongestAdj.get(),
            weakestBorder.get(),
            strongestAdj.get().getArmies() - 1
        ));
    }

    @Override
    public ReinforceEvent getReinforce(Player owner, int armies) {
        Map<Territory,Integer> reinforceMap = new HashMap<>();
        var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        var border = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        var reinforces = Math.floor(playerTerritories.size() / 3);
        reinforces = this.map.getContinentBonus(owner.getId());
        for (int i = 0; i < reinforces; i++) {
            var weakest = border.stream().min(StrategyUtils.TERRITORY_COMPARATOR); // a bit ugly but needed to first check the border
            if (weakest.get().getArmies() >= MAX_ATK_STR) {
                weakest = playerTerritories.stream().min(StrategyUtils.TERRITORY_COMPARATOR);// , then the inland 
                if (weakest.get().getArmies() >= MAX_ATK_STR) {
                    weakest = border.stream().min(StrategyUtils.TERRITORY_COMPARATOR); // and at last reinforce the weakest border if everithing as at least 3 armies
                }
            }
            reinforceMap.merge(weakest.get(), 1,Integer::sum); // sets the number of time a territory is to be reinforced with 1 troop
        }
        return new ReinforceEvent(owner, reinforceMap);
    }

    private Function<Territory,Set<Territory>> createAdjEnemySet(String id) {
        return new Function<Territory,Set<Territory>>() {

            @Override
            public Set<Territory> apply(Territory t) {
                var set = t.getAdjacentIds().stream().map(a -> map.getTerritory(a)).filter(a->!a.getOwnerId().get().equals(id)).collect(Collectors.toSet()); //get enemies
                return set.stream().filter(a -> a.getArmies() < t.getArmies()).collect(Collectors.toSet()); // get enemies weaker than attacker
            }
            
        };
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
