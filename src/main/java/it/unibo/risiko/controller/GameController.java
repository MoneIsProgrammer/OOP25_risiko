package it.unibo.risiko.controller;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.history.HistoryImpl;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.view.GameScene;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Controller for the game part of risiko.
 */
public final class GameController {

    private Roster roster;
    private GameMap map;
    private Scene gameGui;
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

    /**
     * Default constructor for new game.
     * 
     * @param requests players that will play in the game
     */
    public GameController(final List<PlayerRequest> requests, Stage stage) {
        try {
            map = MapLoader.loadDefault();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        //Build map before players then the territories must be assigned
        this.roster = new RosterImpl(requests, map); 
        this.turn = new PlayerTurn(roster);
        this.phase = Phase.SETUP;
        this.stage = stage;
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

    private Consumer<Map<String, Integer>> getReiforceMap() {
        return new Consumer<>() {

            @Override
            public void accept(final Map<String, Integer> t) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'accept'");
            }
        };
    }
    
    public void getTerritories(String to, String from) {

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

}
