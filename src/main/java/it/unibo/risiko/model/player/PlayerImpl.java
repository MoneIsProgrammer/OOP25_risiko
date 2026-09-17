package it.unibo.risiko.model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.risiko.model.deck.Card;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.PlayerStrategy;

/**
 * PlayerImpl implementation of Player.
 */
public final class PlayerImpl implements Player {
    private final String name;
    private final PlayerStrategy strategy;
    private final RisikoColors color;
    private final String id;
    private int reinforces;
    private final List<Card> hand = new ArrayList<>();

    /**
     * Can only be created from {@link PlayerFactory} following a request.
     * 
     * @param color of the player
     * @param name  name of the player
     * @param strategy  strategy that this player will use during its turns
     * @param startingForces the number of armies in the setup phase
     */
    protected PlayerImpl(final RisikoColors color, final String name, final PlayerStrategy strategy, final int startingForces) {
        this.name = name;
        this.strategy = strategy;
        this.color = color;
        this.id = color + name;
        this.reinforces = startingForces;
    }

    @Override
    public PlayerStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public boolean isHuman() {
        return this.strategy instanceof HumanStrategy;
    }

    @Override
    public Optional<AttackEvent> attack() {
        return this.strategy.getAttack(this);
    }

    @Override
    public Optional<MoveEvent> move() {
        return this.strategy.getMove(this);
    }

    @Override
    public ReinforceEvent reinforce(final int armies) {
        return this.strategy.getReinforce(this, armies);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override 
    public RisikoColors getColor() {
        return color;
    }

    @Override
    public ReinforceEvent setupPlacement() {
        final int armiesToPlace = reinforces > 3 ? 3 : reinforces;
        this.reinforces -= armiesToPlace;
        return this.strategy.getSetup(this, armiesToPlace);
    }

    @Override
    public Optional<CardEvent> playCard() {
        return this.strategy.playCards(hand, this);
    }

    @Override
    public void addCard(final Card card) {
        this.hand.add(card);
    }

    @Override
    public MoveEvent getMoveAfterConquest(final String sourceID, final String destinationID) {
        return this.strategy.getMoveAfterConquest(sourceID, destinationID, this);
    }
    
	@Override
	public List<Card> getHand() {
        return this.hand;
    }

    @Override
    public void setArmies(final int armies) {
        this.reinforces = armies;
    }

}
