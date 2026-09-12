package it.unibo.risiko.model.player.strategy.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
 * Player strategy where the ai takes decisions randomly.
 */
public final class RandomStrategy implements PlayerStrategy {

    private final Roster roster;
    private final GameMap map;
    private final Random random;

    /**
     * Default constructor of {@link RandomStrategy}.
     * 
     * @param roster The other players
     * @param map The playing map
     */
    public RandomStrategy(final Roster roster, final GameMap map) {
        this.roster = roster;
        this.map = map;
        this.random = new Random();
    }

    /**
     * Debugging constructor of {@link DefensiveStrategy}.
     * 
     * @param roster The other players
     * @param map The playing map
     * @param seed The seed this strategy will register
     */
    public RandomStrategy(final Roster roster, final GameMap map, final long seed) {
        this.roster = roster;
        this.map = map;
        this.random = new Random(seed);
    }

    @Override
    public Optional<AttackEvent> getAttack(final Player owner) {
        if (this.random.nextInt(10) % 3 == 0) {
            return Optional.empty();
        }
        final var playerTerritories = map.getTerritoriesOf(owner.getId());
        final var border = StrategyUtils.getBorderTerritories(playerTerritories, this.map);
        final var attacker = border.stream().filter(a -> a.getArmies() > 1).findAny();
        if (attacker.isEmpty()) {
            return Optional.empty();
        }
        final var victim = attacker.get().getAdjacentIds().stream()
        .map(this.map::getTerritory)
        .filter(a -> !a.getOwnerId().get().equals(owner.getId()))
        .findAny();
        return Optional.of(new AttackEvent(owner,
            this.roster.getPlayer(victim.get().getOwnerId().get()),
            attacker.get().getArmies() > 3 ? 3 : attacker.get().getArmies() - 1,
            victim.get().getArmies() > 3 ? 3 : victim.get().getArmies(),
            attacker.get(),
            victim.get()
        ));
    }

    @Override
    public Optional<MoveEvent> getMove(final Player owner) {
        if (this.random.nextInt(3) == 0) {
            return Optional.empty();
        }
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var source = playerTerritories.stream().findAny();
        final var destination = source.get().getAdjacentIds().stream()
        .map(this.map::getTerritory)
        .filter(a -> a.getOwnerId().get().equals(owner.getId()))
        .findAny();
        if (destination.isEmpty() || source.get().getArmies() < 3) { // source has no allies or too weak to pass armies
            return Optional.empty();
        }
        final var moveStr = this.random.nextInt(1, source.get().getArmies());
        return Optional.of(new MoveEvent(owner, source.get(), destination.get(), moveStr));
    }

    @Override
    public ReinforceEvent getReinforce(final Player owner, final int armies) {
        final Map<Territory, Integer> reinforceMap = new HashMap<>();
        final var playerTerritories = this.map.getTerritoriesOf(owner.getId());
        final var reinforcements = Math.floor(playerTerritories.size() / 3) + this.map.getContinentBonus(owner.getId());
        for (int i = 0; i < reinforcements; i++) {
            reinforceMap.merge(playerTerritories.stream().findAny().get(), 1, Integer::sum);
        }
        return new ReinforceEvent(owner, reinforceMap);
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
}
