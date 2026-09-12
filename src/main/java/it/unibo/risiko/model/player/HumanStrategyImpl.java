package it.unibo.risiko.model.player;

import java.util.ArrayList;
import java.util.Collection;
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
import it.unibo.risiko.model.player.strategy.HumanStrategy;

/**
 * HumanStrategyImpl
 */
public class HumanStrategyImpl implements HumanStrategy{


    private final Roster roster;
    private final GameMap map; // i don't need it for now, remove if in final build isn't used
    private final AttackBuilder attackBuilder = new AttackBuilder();
    private final ReinforceBuilder reinforceBuilder = new ReinforceBuilder();
    private final MoveBuilder moveBuilder = new MoveBuilder();
    private final CardPlayBuilder cardBuilder = new CardPlayBuilder();

    public HumanStrategyImpl(Roster roster, GameMap map) {
        this.roster = roster;
        this.map = map;
    }

    @Override
    public Optional<AttackEvent> getAttack(Player owner) {
        return Optional.of(this.attackBuilder.build(owner)); // Optionals are 
    }

    @Override
    public Optional<MoveEvent> getMove(Player owner) {
        return Optional.of(this.moveBuilder.build(owner));
    }

    @Override
    public ReinforceEvent getReinforce(Player owner, int armies) {
        return this.reinforceBuilder.build(owner, armies);
    }

    @Override
    public void attackSource(Territory territory) {
        this.attackBuilder.setAttackSource(territory);
    }

    @Override
    public void attackDestination(Territory territory) {
        this.attackBuilder.setDefender(territory);
        this.attackBuilder.setVictim(this.roster.getPlayer(territory.getOwnerId().get()));
        this.attackBuilder.setDefenderStrenght(territory.getArmies() > 3 ? 3 : territory.getArmies());
    }

    @Override
    public void attackStrenght(int strength) {
        this.attackBuilder.setAttackStrenght(strength);
    }

    @Override
    public void reinforce(Map<Territory, Integer> reinfoceMap) {
        this.reinforceBuilder.setReinforcements(reinfoceMap);
    }

    @Override
    public void moveSource(Territory territory) {
        this.moveBuilder.setSource(territory);
    }

    @Override
    public void moveDestination(Territory territory) {
        this.moveBuilder.setDestination(territory);
    }

    @Override
    public void moveStrenght(int strength) {
        this.moveBuilder.setTroopsMoved(strength);
    }

    @Override
    public ReinforceEvent getSetup(Player owner, int startingForces) {
        return this.reinforceBuilder.build(owner, startingForces);
    }

    @Override
    public void setupPlacement(Map<Territory,Integer> placement) {
        this.reinforceBuilder.setReinforcements(placement);
    }

    @Override
    public Optional<CardEvent> playCards(List<Card> hand, Player owner) {
        return Optional.of(this.cardBuilder.build(owner));
    }

    @Override 
    public void cardsToPlay(Collection<Card> combo) {
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

    private class AttackBuilder {
        private Player victim = null;
        private Integer attackStrenght = null;
        private Integer defenderStrenght = null;
        private Territory source = null;
        private Territory destination = null;

        public void setAttackStrenght(int strength) {
            this.attackStrenght = strength;
        }

        public void setDefenderStrenght(int strength) {
            this.defenderStrenght = strength;
        }

        public void setAttackSource(Territory territory) {
            this.source = territory;
        }

        public void setDefender(Territory territory) {
            this.destination = territory;
        }

        public void setVictim(Player victim) {
            this.victim = victim;
        }

        public boolean canBuild() {
            return this.attackStrenght != null 
            && this.defenderStrenght != null
            && this.destination != null
            && this.source != null
            && this.victim != null;
        }

        public AttackEvent build(Player owner) {
            if (!canBuild()) {
                throw new IllegalStateException("attack is not complete");
            }
            var out = new AttackEvent(owner, this.victim, this.attackStrenght, this.defenderStrenght, this.source,this.destination);
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

    private class ReinforceBuilder {
        Map<Territory, Integer> reinforceMap = null;

        void setReinforcements(Map<Territory, Integer> reinforcements) {
            this.reinforceMap.putAll(reinforcements);
        }
        
        public ReinforceEvent build(Player owner, int armies) {
            if (reinforceMap.values().stream().reduce(Integer::sum).get() != armies && canBuild()) {
                throw new IllegalStateException("The reinforcements can't be different from declared armies"); //this is a check as i can't check if map is empty due to the minimum reinforce avabile are 0
            }
            var out = new ReinforceEvent(owner, reinforceMap);
            this.reinforceMap = null;
            return out;
        }

        public  boolean canBuild() {
            return reinforceMap != null;
        }
    }

    private class MoveBuilder {

        private Territory source = null;
        private Territory destination = null;
        private Integer troopsMoved = null;

        public MoveEvent build(Player owner) {
            if (!canBuild()) {
                throw new IllegalStateException("Move is not complete");
            }
            var out = new MoveEvent(owner,this.source,this.destination,this.troopsMoved);
            clear();
            return out;
        }

        public  boolean canBuild() {
            return this.source != null
            && this.destination != null
            && this.troopsMoved != null;
        }

        private void clear() {
            this.source = null;
            this.destination = null;
            this.troopsMoved = null;
        }

        public void setSource(Territory source) {
            this.source = source;
        }

        public void setDestination(Territory destination) {
            this.destination = destination;
        }

        public void setTroopsMoved(int troopsMoved) {
            this.troopsMoved = troopsMoved;
        }
    }

    private class CardPlayBuilder {
    
        private List<Collection<Card>> played;
        private int armies;

        public CardEvent build(Player owner) {

            var out = new CardEvent(this.played, owner, armies);
            clear();
            return out;
        }

        public void addCombo(Collection<Card> combo) {
            if (played == null) {
                played = new ArrayList<>();
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

        public boolean canBuild() {
            return this.played != null;
        }


    }
}
