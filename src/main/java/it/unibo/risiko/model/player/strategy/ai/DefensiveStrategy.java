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
public final class DefensiveStrategy implements PlayerStrategy {

    private static final int MAX_ATK_STR = 3;
    private final Roster roster;
    private final GameMap map;

    /**
     * Default constructor of {@link DefensiveStrategy}.
     * 
     * @param roster The other players
     * @param map The playing map
     */
    public DefensiveStrategy(final Roster roster, final GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    //attacks only when has more than 3 troops and his troops > his target troops
    public Optional<AttackEvent> getAttack(final Player owner) { 
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var borders = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        final var validAttacks = borders.stream()// this.map of territories and their adjacent enemies
        .filter(a -> a.getArmies() > MAX_ATK_STR)
        .collect(Collectors.toMap(a -> a, createAdjEnemySet(owner.getId())));
        validAttacks.entrySet()
        .removeIf(a -> a.getValue().isEmpty()); // prune entries where the attacker has no valid targets
        if (validAttacks.isEmpty()) {
            return Optional.empty();
        }
        final var strongestAttacker = validAttacks.keySet().stream().max(StrategyUtils.TERRITORY_COMPARATOR).get();
        final var weakestVictim = validAttacks.get(strongestAttacker).stream().min(StrategyUtils.TERRITORY_COMPARATOR).get();
        return Optional.of(new AttackEvent(owner,
            this.roster.getPlayer(weakestVictim.getOwnerId().get()),
            // this will always result in max armies, useful if the attack policy changes
            strongestAttacker.getArmies() > MAX_ATK_STR ? MAX_ATK_STR : strongestAttacker.getArmies() - 1,
            // to simplify defenders always defend with all their armies (MAX _3 min 1)
            weakestVictim.getArmies() > MAX_ATK_STR ? MAX_ATK_STR : weakestVictim.getArmies(), 
            strongestAttacker,
            weakestVictim
        ));
    }

    @Override
    public Optional<MoveEvent> getMove(final Player owner) { //println only dirty debugging purposes
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var border = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        final var weakestBorder = border.stream()
        .filter(a -> StrategyUtils.notIsolated(a, map))
        .min(StrategyUtils.TERRITORY_COMPARATOR);
        if (weakestBorder.isEmpty()) {
            System.out.println("no weakest border");
            return Optional.empty();
        }
        //strongest adj non border
        final var strongestAdj = weakestBorder.get().getAdjacentIds().stream()
        .map(this.map::getTerritory)
        .filter(a -> !border.contains(a))
        .filter(a -> a.getOwnerId().get().equals(owner.getId()))
        .max(StrategyUtils.TERRITORY_COMPARATOR);
        if (strongestAdj.isEmpty() || strongestAdj.get().getArmies() < 3) {
            System.out.println("no Strongest adj");
            return Optional.empty();
        }
        return Optional.of(new MoveEvent(owner,
            strongestAdj.get(),
            weakestBorder.get(),
            strongestAdj.get().getArmies() - 2
        ));
    }

    @Override
    public ReinforceEvent getReinforce(final Player owner, final int armies) {
        final Map<Territory, Integer> reinforceMap = new HashMap<>(); //internal rapresentation of territories and troops
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        for (Territory territory : playerTerritories) {
            reinforceMap.put(territory, territory.getArmies());
        }
        final var border = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        for (int i = 0; i < armies; i++) {
            var weakest = reinforceMap.entrySet().stream()
            .filter(a -> border.contains(a.getKey()))
            .min((a,b) -> Integer.compare(a.getValue(), b.getValue())); // a bit ugly but needed to first check the border
            if (weakest.get().getValue() >= MAX_ATK_STR) {
                weakest = reinforceMap.entrySet().stream()
                .filter(a -> !border.contains(a.getKey()))
                .min((a,b) -> Integer.compare(a.getValue(), b.getValue())); // ,then the inland 
                if (weakest.get().getValue() >= MAX_ATK_STR) {
                    // and at last reinforce the weakest border if everithing as at least 3 armies
                    weakest = reinforceMap.entrySet().stream()
                    .filter(a -> border.contains(a.getKey()))
                    .min((a,b) -> Integer.compare(a.getValue(), b.getValue())); 
                }
            }
            // sets the number of time a territory is to be reinforced with 1 troop
            reinforceMap.merge(weakest.get().getKey(), 1, Integer::sum);
        }
        reinforceMap.replaceAll((k,v) -> v - k.getArmies());
        reinforceMap.entrySet().removeIf(a -> a.getValue() == 0);
        return new ReinforceEvent(owner, reinforceMap);
    }

    private Function<Territory, Set<Territory>> createAdjEnemySet(final String id) {
        return new Function<>() {

            @Override
            public Set<Territory> apply(final Territory t) {
                final var set = t.getAdjacentIds().stream()
                .map(map::getTerritory)
                .filter(a -> !a.getOwnerId().get().equals(id))
                .collect(Collectors.toSet()); //get enemies
                return set.stream()
                .filter(a -> a.getArmies() < t.getArmies())
                .collect(Collectors.toSet()); // get enemies weaker than attacker
            }
        };
    }

    @Override
    public ReinforceEvent getSetup(final Player owner, final int startingForces) {
        return this.getReinforce(owner, startingForces);
    }

    @Override
    public Optional<CardEvent> playCards(final List<Card> hand, final Player owner) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playCards'");
    }

    @Override
    public MoveEvent getMoveAfterConquest(String sourceID, String destinationID, Player owner) {
        var source = this.map.getTerritory(sourceID);
        var destination = this.map.getTerritory(destinationID);
        var troopsToMove = source.getArmies() - 1;
        if (troopsToMove > 1) {
            if (source.getAdjacentIds().stream().map(this.map::getTerritory).anyMatch(a -> a.getOwnerId().get() != owner.getId())) {
                troopsToMove = Math.floorDiv(troopsToMove, 2); //if has enemy near halve the troops
            }
            else {
                //else move all but 2
                troopsToMove =- 1;
            }
        }
        return new MoveEvent(owner, source, destination, troopsToMove);
    }

}
