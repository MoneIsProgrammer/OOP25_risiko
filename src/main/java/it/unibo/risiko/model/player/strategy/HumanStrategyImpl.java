package it.unibo.risiko.model.player.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.Roster;

/**
 * Implementation of {@link HumanStrategy}.
 */
public final class HumanStrategyImpl implements HumanStrategy {

    private final Roster roster;
    private final GameMap map;
    private final AttackBuilder attackBuilder = new AttackBuilder();
    private final ReinforceBuilder reinforceBuilder = new ReinforceBuilder();
    private final MoveBuilder moveBuilder = new MoveBuilder();
    private final CardPlayBuilder cardBuilder = new CardPlayBuilder();

    /**
     * @param roster The other players this strategy will refer to
     * @param map the map this strategy will refer to
     */
    public HumanStrategyImpl(final Roster roster, final GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    public Optional<AttackEvent> getAttack(final Player owner) {
        return Optional.of(this.attackBuilder.build(owner)); // Optionals are 
    }

    @Override
    public Optional<MoveEvent> getMove(final Player owner) {
        return Optional.of(this.moveBuilder.build(owner));
    }

    @Override
    public ReinforceEvent getReinforce(final Player owner, final int armies) {
        return this.reinforceBuilder.build(owner, armies);
    }

    @Override
    public void attackSource(final Territory territory) {
        this.attackBuilder.setAttackSource(territory);
    }

    @Override
    public void attackDestination(final Territory territory) {
        this.attackBuilder.setDefender(territory);
        this.attackBuilder.setVictim(this.roster.getPlayer(territory.getOwnerId().get()));
        this.attackBuilder.setDefenderStrenght(territory.getArmies() > 3 ? 3 : territory.getArmies());
    }

    @Override
    public void attackStrenght(final int strength) {
        this.attackBuilder.setAttackStrenght(strength);
    }

    @Override
    public void reinforce(final Map<Territory, Integer> reinfoceMap) {
        this.reinforceBuilder.setReinforcements(reinfoceMap);
    }

    @Override
    public void moveSource(final Territory territory) {
        this.moveBuilder.setSource(territory);
    }

    @Override
    public void moveDestination(final Territory territory) {
        this.moveBuilder.setDestination(territory);
    }

    @Override
    public void moveStrenght(final int strength) {
        this.moveBuilder.setTroopsMoved(strength);
    }

    @Override
    public ReinforceEvent getSetup(final Player owner, final int startingForces) {
        return this.reinforceBuilder.build(owner, startingForces);
    }

    @Override
    public void setupPlacement(final Map<Territory,Integer> placement) {
        this.reinforceBuilder.setReinforcements(placement);
    }

    @Override
    public Optional<CardEvent> playCards(final List<Card> hand, final Player owner) {
        return Optional.of(this.cardBuilder.build(owner));
    }

    @Override 
    public void cardsToPlay(final Collection<Card> combo) {
        this.cardBuilder.addCombo(combo);
    }

    @Override
    public boolean canCreateAttack() {
        return this.attackBuilder.canBuild();
    }

    @Override 
    public boolean canCreateReinforce() {
        return this.reinforceBuilder.canBuild();
    }

    @Override 
    public boolean canPlayCards() {
        return this.cardBuilder.canBuild();
    }

    @Override 
    public boolean canCreateMove() {
        return this.moveBuilder.canBuild();
    }

    @Override
    public void flush() {
        this.attackBuilder.clear();
        this.cardBuilder.clear();
        this.moveBuilder.clear();
        this.reinforceBuilder.clear();
    }

    private final class AttackBuilder {
        private Player victim;
        private Integer attackStrenght;
        private Integer defenderStrenght;
        private Territory source;
        private Territory destination ;

        private void setAttackStrenght(final int strength) {
            if (strength < 1) {
                throw new IllegalArgumentException("can't attack with " + strength + " armies");
            }
            this.attackStrenght = strength;
        }

        private void setDefenderStrenght(final int strength) {
            if (strength < 1) {
                throw new IllegalArgumentException("can't defend with " + strength + " armies");
            }
            this.defenderStrenght = strength;
        }

        private void setAttackSource(final Territory territory) {
            this.source = territory;
        }

        private void setDefender(final Territory territory) {
            this.destination = territory;
        }

        private void setVictim(final Player victim) {
            this.victim = victim;
        }

        private boolean canBuild() {
            return this.attackStrenght != null 
            && this.defenderStrenght != null
            && this.destination != null
            && this.source != null
            && this.victim != null;
        }

        private AttackEvent build(final Player owner) {
            if (!canBuild()) {
                throw new IllegalStateException("attack is not complete");
            }
            final var out = new AttackEvent(owner,
                this.victim, 
                this.attackStrenght, 
                this.defenderStrenght, 
                this.source,
                this.destination);
            clear();
            return out;
        }

        private void clear() {
            this.attackStrenght = null;
            this.defenderStrenght = null;
            this.destination = null;
            this.source = null;
            this.victim = null;
        }
    }

    private final class ReinforceBuilder {
        private Map<Territory, Integer> reinforceMap;

        private void setReinforcements(final Map<Territory, Integer> reinforcements) {
            if (this.reinforceMap == null) {
                this.reinforceMap = new HashMap<>();
            }
            for (int val : reinforcements.values()) {
                if (val < 1) {
                    throw new IllegalArgumentException("cant reinforce with " + val + " troops");
                }
            }
            this.reinforceMap.putAll(reinforcements);
        }
        
        public void clear() {
            this.reinforceMap = null;
        }

		private ReinforceEvent build(final Player owner, final int armies) {
            if (reinforceMap.values().stream().reduce(Integer::sum).get() != armies && canBuild()) {
                throw new IllegalStateException("The reinforcements can't be different from declared armies");
                 //this is a check as i can't check if map is empty due to the minimum reinforce avabile are 0
            }
            final var out = new ReinforceEvent(owner, reinforceMap);
            clear();
            return out;
        }

        private  boolean canBuild() {
            return reinforceMap != null;
        }
    }

    private final class MoveBuilder {

        private Territory source;
        private Territory destination;
        private Integer troopsMoved;

        private MoveEvent build(final Player owner) {
            if (!canBuild()) {
                throw new IllegalStateException("Move is not complete");
            }
            final var out = new MoveEvent(owner,
                this.source,
                this.destination,
                this.troopsMoved);
            clear();
            return out;
        }

        private  boolean canBuild() {
            return this.source != null
            && this.destination != null
            && this.troopsMoved != null;
        }

        private void clear() {
            this.source = null;
            this.destination = null;
            this.troopsMoved = null;
        }

        private void setSource(final Territory source) {
            this.source = source;
        }

        private void setDestination(final Territory destination) {
            this.destination = destination;
        }

        private void setTroopsMoved(final int troopsMoved) {
            if (troopsMoved < 1) {
                throw new IllegalArgumentException("can't move " + troopsMoved + " armies");
            }
            this.troopsMoved = troopsMoved;
        }
    }

    private final class CardPlayBuilder {
    
        private List<Collection<Card>> played;
        private int armies;

        private CardEvent build(final Player owner) {

            final var out = new CardEvent(this.played, owner, armies);
            clear();
            return out;
        }

        private void addCombo(final Collection<Card> combo) {
            if (played == null) {
                played = new ArrayList<>();
            }
            if (combo.size() != 3) {
                throw new IllegalArgumentException("The combo must be of only 3 cards");
            }
            played.add(combo);
            calculateArmies();
        }

        private void calculateArmies() {
            //TODO missing method to get armies from combo
        }

        private void clear() {
            this.armies = 0;
            this.played = null;
        }

        private boolean canBuild() {
            return this.played != null;
        }


    }

    @Override
    public MoveEvent getMoveAfterConquest(String sourceID, String destinationID, Player owner) {
        if (this.moveBuilder.troopsMoved == null) {
            //default case instead of trowing an exception now moves the least amount of troops,
            //quickens this method call if player wants to do nothing
            this.moveBuilder.setTroopsMoved(1);
        }
        this.moveBuilder.setDestination(this.map.getTerritory(destinationID));
        this.moveBuilder.setSource(this.map.getTerritory(sourceID));
        return this.moveBuilder.build(owner);
    }
}
