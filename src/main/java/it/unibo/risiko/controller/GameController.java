package it.unibo.risiko.controller;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.battle.BattleResult;
import it.unibo.risiko.model.deck.TerritoriesDeck;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.AttackResultEvent;
import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.history.HistoryImpl;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.view.GameScene;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;

/**
 * The Controller for the game part of risiko.
 */
public final class GameController {

    // you attack with 3 armies at most
    private static final int MAX_ATTACK_ARMIES = 3;

    private Roster roster;
    private GameMap map;
    private History history = new HistoryImpl();
    private PlayerTurn turn;
    private Phase phase;
    private String sourceId;
    private String destinationId;
    // activate button to commit the action if it can be generated, both this and the army are used to check may be moved
    public final BooleanProperty ableToBuild = new SimpleBooleanProperty(true);
    //to show player how many armies has to place or wants to utilize
    public final IntegerProperty armyCounter = new SimpleIntegerProperty(0);
    // set this to the maximum troops utilizable for the action, limits if action can be launched by controller parameteters
    public final IntegerProperty maxArmyforAction = new SimpleIntegerProperty(6);
    public Consumer<Integer> getStrenght;
    private GameScene view;
    private Stage stage;
    // the armies put with the clicks in the reinforce or in the setup, for each territory
    private final Map<Territory, Integer> placements = new HashMap<>();

    /**
     * Default constructor for new game.
     * 
     * @param requests players that will play in the game
     */
    public GameController(final List<PlayerRequest> requests, Stage stage) {
        try {
            this.map = MapLoader.loadDefault();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        //Build map before players then the territories must be assigned
        this.roster = new RosterImpl(requests, this.map); 
        this.turn = new PlayerTurn(roster);
        this.phase = Phase.SETUP;
        this.stage = stage;
        TerritoriesDeck territoryDeck = new TerritoriesDeck();
        territoryDeck.populateTerritoryDeck();
        for (int i = 0; i < 42; i++) {
            var curr = this.turn.next();
            var terr = territoryDeck.dealCard();
            System.out.println(terr.getTerritory().getTerritoryName());
            this.map.getTerritory(terr.getTerritory().getTerritoryId()).addArmies(1);
            this.map.getTerritory(terr.getTerritory().getTerritoryId()).setOwner(curr.getId());
        }
        // the counter of the buttons says how many armies attack or move
        this.getStrenght = armies -> armiesChosen(armies);
        // a new reinforce starts from zero
        this.turn.addPropertyChangeListener(event -> this.placements.clear());
        this.turn.addPlayerChangeListener(event -> this.placements.clear());
    }

    private Consumer<Integer> getStrenght() {
        // TODO Auto-generated method stub
        return new Consumer<>() {
            //with ArmyCounter this may be useless
            @Override
            public void accept(final Integer t) {
                maxArmyforAction.set(10);
                System.out.println(t); //dirty testing, feel free to remove
            }

        };
    }

    private Consumer<Entry<String, Integer>> getReiforceMap() {
        return new Consumer<>() {

            @Override
            public void accept(final Entry<String, Integer> t) {
                
            }
        };
    }
    
    // same order as the map clicks: first where it starts, then where it goes
    @SuppressFBWarnings("PA_PUBLIC_MUTABLE_OBJECT_ATTRIBUTE") // the buttons read the max from here
    public void getTerritories(final String from, final String to) {
        if (!humanPlays()) {
            return;
        }
        final var source = this.map.getTerritory(from);
        final var destination = this.map.getTerritory(to);
        if (this.turn.getCurrentPhase() == Phase.ATTACK) {
            humanStrategy().attackSource(source);
            humanStrategy().attackDestination(destination);
            // one army has to stay home
            this.maxArmyforAction.set(Math.min(MAX_ATTACK_ARMIES, source.getArmies() - 1));
        } else {
            humanStrategy().moveSource(source);
            humanStrategy().moveDestination(destination);
            // you can move all the armies but one
            this.maxArmyforAction.set(source.getArmies() - 1);
        }
    }

    /**
     * Receives the territory clicked in the reinforce or in the setup, where the armies go.
     * Every click puts there one of the armies of the counter.
     *
     * @param territoryId id of the territory
     */
    public void placementChosen(final String territoryId) {
        // no armies left to place
        if (!humanPlays() || this.armyCounter.get() < 1) {
            return;
        }
        final var territory = this.map.getTerritory(territoryId);
        // one more army on this territory
        this.placements.put(territory, this.placements.getOrDefault(territory, 0) + 1);
        this.armyCounter.set(this.armyCounter.get() - 1);
        // the strategy keeps one number for each territory, so i give it the new total
        final var placement = Map.of(territory, this.placements.get(territory));
        if (this.turn.getCurrentPhase() == Phase.SETUP) {
            humanStrategy().setupPlacement(placement);
        } else {
            humanStrategy().reinforce(placement);
        }
    }

    // the armies of the counter when confirm is pressed: how many attack or how many move
    private void armiesChosen(final int armies) {
        // the strategy doesn't take 0 armies
        if (!humanPlays() || armies < 1) {
            return;
        }
        if (this.turn.getCurrentPhase() == Phase.ATTACK) {
            humanStrategy().attackStrenght(armies);
        } else if (this.turn.getCurrentPhase() == Phase.MOVE) {
            humanStrategy().moveStrenght(armies);
        }
    }

    // only a human gives the input, while a bot plays the clicks and the buttons do nothing
    private boolean humanPlays() {
        return this.turn.getCurrentPlayer().isHuman();
    }

    // isHuman says the strategy is a HumanStrategy, so the cast is safe
    private HumanStrategy humanStrategy() {
        return (HumanStrategy) this.turn.getCurrentPlayer().getStrategy();
    }

    public void registerView(GameScene gameScene) {
        this.view = gameScene;
        this.view.start(roster, map, history, this.stage);
    }

    public void advancePhase() {
        this.turn.advancePhase();
    }

    public void addListener(PropertyChangeListener listener) {
        turn.addPropertyChangeListener(listener);
    }

    /**
     * Adds someone to be told when the player of the turn changes.
     *
     * @param listener the listener to add
     */
    public void addPlayerListener(final PropertyChangeListener listener) {
        turn.addPlayerChangeListener(listener);
    }

    /**
     * Sends an event to the history and to the map.
     *
     * @param event the event
     */
    public void publish(final Event event) {
        this.history.addEvent(event);
        // the view is null until registerView
        if (this.view != null) {
            this.view.onGameEvent(event);
        }
    }

    /**
     * Same as publish but for an attack, it also shows the dice.
     *
     * @param attack the attack
     * @param result the result of the battle
     */
    public void publishBattle(final AttackEvent attack, final BattleResult result) {
        final var resultEvent = new AttackResultEvent(attack, result);
        publish(resultEvent);
        if (this.view != null) {
            this.view.onBattleResult(result);
        }
    }

    public void confirmAction() {

    }

    public void startGame() {
        while (true) {
            
        }
    }

    private void setupAction() {}

}
