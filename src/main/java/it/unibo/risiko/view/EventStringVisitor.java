package it.unibo.risiko.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.AttackResultEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.EventVisitor;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.map.Territory;

/**
 * EventStringVisitor visitor to get descriptive strings for the events.
 */
public final class EventStringVisitor implements EventVisitor<List<String>> {

    /**
     * Just creates the object, to be used must be passed to events
     */
    public EventStringVisitor() {
    }

    @Override
    public List<String> visit(final AttackEvent event) {
        final String out = event.attacker().getName() + " attacks "
        + event.defender().getName()
        + " from " + event.attackSource().getName()
        + " with " + event.attackerStrength() 
        + " troops to " + event.attackDestination().getName()
        + " defending with " + event.defenderStrength() + " units";
        return List.of(out);
    }

    @Override
    public List<String> visit(final AttackResultEvent event) {
        final String attackerRolls = event.attack().attacker().getName() + " rolled " + event.result().getAttackerRolls().toString();
        final String defenderRolls = event.attack().defender().getName() + " rolled: " + event.result().getDefenderRolls().toString();
        final String attacker = event.attack().attacker().getName() + " lost " + event.result().getAttackerLosses() + " troops";
        final String defender = event.attack().defender().getName() + " lost " + event.result().getDefenderLosses() + " troops";
        if (event.result().isConquered()) {
            final String conquered = event.attack().attacker() + " conquered " + event.attack().attackDestination().getName();
            return List.of(attackerRolls, attacker, defenderRolls, defender, conquered);
        }
        return List.of(attackerRolls, attacker, defenderRolls, defender);
    }

    @Override
    public List<String> visit(final MoveEvent event) {
        final String out = event.player().getName() 
        + " moved " + event.troopsMoved() 
        + " troops from " + event.sourceTerritory().getName() 
        + " to " + event.destinationTerritory().getName();
        return List.of(out);
    }

    @Override
    public List<String> visit(final ReinforceEvent event) {
        final List<String> out = new ArrayList<>();
        for (final Entry<Territory, Integer> entry : event.reinforcement().entrySet()) {
            out.add(event.player().getName() + " added " + entry.getValue() + " troops to " + entry.getKey().getName());
        }
        return List.copyOf(out);
    }

    @Override
    public List<String> visit(CardEvent cardEvent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

}
