package it.unibo.risiko.controller;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.risiko.model.battle.BattleResult;
import it.unibo.risiko.model.battle.CombatSystem;
import it.unibo.risiko.model.battle.CombatSystemImpl;
import it.unibo.risiko.model.battle.Dice;
import it.unibo.risiko.model.battle.RandomDice;
import it.unibo.risiko.model.deck.DrawCard;
import it.unibo.risiko.model.deck.ObjectivesDeck;
import it.unibo.risiko.model.deck.TerritoriesDeck;
import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.AttackResultEvent;
import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.history.History;
import it.unibo.risiko.model.history.HistoryImpl;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.MapLoader;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.Player;
import it.unibo.risiko.model.player.PlayerRequest;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.model.player.Roster;
import it.unibo.risiko.model.player.RosterImpl;
import it.unibo.risiko.model.player.strategy.HumanStrategy;
import it.unibo.risiko.model.player.strategy.StrategyUtils;
import it.unibo.risiko.model.turn.MovePhase;
import it.unibo.risiko.model.turn.Phase;
import it.unibo.risiko.model.turn.VictoryCheck;
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
    // in the reinforce you get one army every 3 territories, but never less than 3
    private static final int TERRITORIES_FOR_ONE_ARMY = 3;
    private static final int MIN_REINFORCEMENTS = 3;
    // a bot stops attacking after this many attacks, so its turn always ends
    private static final int MAX_BOT_ATTACKS = 10;
    // at the start 35 armies with 3 players, 30 with 4, 25 with 5 and 20 with 6
    private static final int STARTING_ARMIES_BASE = 50;
    private static final int FEWER_ARMIES_PER_PLAYER = 5;

    private Roster roster;
    private GameMap map;
    private History history = new HistoryImpl();
    public PlayerTurn turn;
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
    // the real dice, the fixed ones are only for the tests of the combat
    private final CombatSystem combat = new CombatSystemImpl(new RandomDice());
    private final VictoryCheck victory;
    // how many players still have to place their starting armies
    private int playersToSetUp;
    private final DrawCard draw;
    private final MovePhase elimination;
    // conquered this turn
    private boolean conquered;
    // target killed
    private boolean targetDestroyed;

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
        this.victory = new VictoryCheck(this.map);
        this.draw = new DrawCard(this.turn);
        this.elimination = new MovePhase(this.map, this.roster, this.victory, this.turn);
        dealObjectives();
        // the counter of the buttons says how many armies attack or move
        this.getStrenght = armies -> armiesChosen(armies);
        // a new reinforce starts from zero, and the counter says how many armies there are to place
        this.turn.addPropertyChangeListener(event -> {
            this.placements.clear();
            if (event.getNewValue() == Phase.REINFORCE) {
                this.armyCounter.set(reinforcementsOf(this.turn.getCurrentPlayer()));
            }
        });
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
        // the army goes on the map now, so you see it while you click
        territory.addArmies(1);
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
        final var player = this.turn.getCurrentPlayer();
        if (this.turn.getCurrentPhase() == Phase.ATTACK) {
            humanStrategy().attackStrenght(armies);
            // the attack is complete, so it happens now
            if (humanStrategy().canCreateAttack()) {
                final var attack = humanStrategy().getAttack(player);
                if (attack.isPresent() && isValidAttack(attack.get())) {
                    executeAttack(attack.get());
                }
            }
        } else if (this.turn.getCurrentPhase() == Phase.MOVE) {
            humanStrategy().moveStrenght(armies);
            // there is only one move in a turn, after it the turn passes
            if (humanStrategy().canCreateMove()) {
                final var move = humanStrategy().getMove(player);
                if (move.isPresent() && isValidMove(move.get())) {
                    executeMove(move.get());
                    passTurn();
                }
            }
        }
    }

    // only a human gives the input: while a bot plays, or after the end, clicks and buttons do nothing
    private boolean humanPlays() {
        return !this.turn.isGameOver() && this.turn.getCurrentPlayer().isHuman();
    }

    // isHuman says the strategy is a HumanStrategy, so the cast is safe
    private HumanStrategy humanStrategy() {
        return (HumanStrategy) this.turn.getCurrentPlayer().getStrategy();
    }

    public void registerView(GameScene gameScene) {
        this.view = gameScene;
        this.view.start(roster, map, history, this.stage);
        // the view is listening now, so the game can start
        startGame();
    }

    /**
     * Goes to the next phase, it's the done button. You can't leave the reinforce with armies
     * still to place, and after the move the turn passes.
     */
    public void advancePhase() {
        if (!humanPlays()) {
            return;
        }
        final var current = this.turn.getCurrentPhase();
        if (current == Phase.SETUP) {
            // all your starting armies have to be placed before the next player
            if (this.armyCounter.get() == 0) {
                placeReinforcements();
                endSetupTurn();
            }
            return;
        }
        if (current == Phase.REINFORCE) {
            // you have to place all the armies before going on
            if (this.armyCounter.get() > 0) {
                return;
            }
            placeReinforcements();
        }
        if (current == Phase.MOVE) {
            // the move is the last phase, now it's the turn of the next player
            passTurn();
        } else {
            this.turn.advancePhase();
        }
    }

    /**
     * Says who is playing now, the view uses it for the objective.
     *
     * @return the player of the turn
     */
    public Player getCurrentPlayer() {
        return this.turn.getCurrentPlayer();
    }

    /**
     * Says who won.
     *
     * @return the winner, or null while the game goes on
     */
    public Player getWinner() {
        return this.turn.getWinner();
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

    /**
     * Starts the game with the setup: every territory has the army it got when the territories
     * were dealt, and one player at a time places the rest of its starting armies.
     */
    public void startGame() {
        this.playersToSetUp = this.roster.getAllPlayers().size();
        // the next player places first, and so the view is told who it is
        this.turn.next();
        setupTurn();
    }

    // the player of the turn places its starting armies: a human with the clicks, a bot by itself
    private void setupTurn() {
        final var player = this.turn.getCurrentPlayer();
        final int reserve = startingArmies() - this.map.getTerritoriesOf(player.getId()).size();
        if (player.isHuman()) {
            // the counter shows the reserve, done goes on when it's 0
            this.armyCounter.set(reserve);
        } else {
            spreadArmies(player, reserve);
            endSetupTurn();
        }
    }

    // the player placed everything: now the next one, or the turns start when everybody is done
    private void endSetupTurn() {
        this.playersToSetUp--;
        // after the last one we are back to the first one, who plays first
        this.turn.next();
        if (this.playersToSetUp > 0) {
            setupTurn();
        } else {
            this.turn.setupFinished();
            letBotsPlay();
        }
    }

    // every player gets a secret objective, the victory is checked on it
    private void dealObjectives() {
        final var objectives = new ObjectivesDeck();
        objectives.createObjectiveDeck();
        for (final Player player : this.roster.getAllPlayers()) {
            player.setObjective(objectives.getObjectiveCard());
            // impossible target, 24 territories
            final var objective = player.getObjective().getObjective();
            if (this.elimination.eliminateColorObjective(objective)
                    && !colorInGame(this.elimination.playerTargetColour(objective), player)) {
                player.setNewObjective();
            }
        }
    }

    // someone else has it
    private boolean colorInGame(final RisikoColors color, final Player player) {
        for (final Player other : this.roster.getAllPlayers()) {
            if (!other.equals(player) && other.getColor() == color) {
                return true;
            }
        }
        return false;
    }

    // 35 armies with 3 players, 30 with 4, 25 with 5 and 20 with 6
    private int startingArmies() {
        return STARTING_ARMIES_BASE - FEWER_ARMIES_PER_PLAYER * this.roster.getAllPlayers().size();
    }

    // a bot puts its armies on its territories one at a time, like dealing cards
    private void spreadArmies(final Player bot, final int armies) {
        final List<Territory> owned = new ArrayList<>(this.map.getTerritoriesOf(bot.getId()));
        final Map<Territory, Integer> added = new HashMap<>();
        for (int i = 0; i < armies; i++) {
            final var territory = owned.get(i % owned.size());
            territory.addArmies(1);
            added.put(territory, added.getOrDefault(territory, 0) + 1);
        }
        // the history and the map are told too
        publish(new ReinforceEvent(bot, added));
    }

    // one army every 3 territories, at least 3, plus the bonus of the continents and cards
    private int reinforcementsOf(final Player player) {
        final int territories = this.map.getTerritoriesOf(player.getId()).size();
        return Math.max(MIN_REINFORCEMENTS, territories / TERRITORIES_FOR_ONE_ARMY)
                + this.map.getContinentBonus(player.getId())
                + playCards(player);
    }

    // auto trade, like the bots
    private int playCards(final Player player) {
        final var played = StrategyUtils.genericCardPlay(player.getHand(), player, this.map);
        if (played.isEmpty()) {
            return 0;
        }
        // remove and log
        player.getHand().removeAll(played.get().played());
        publish(played.get());
        return played.get().gainedArmies();
    }

    // at the end of the reinforce everybody is told where the armies went,
    // they are already on the map because every click puts one there
    private void placeReinforcements() {
        int placed = 0;
        for (final int armies : this.placements.values()) {
            placed = placed + armies;
        }
        if (placed > 0) {
            publish(humanStrategy().getReinforce(this.turn.getCurrentPlayer(), placed));
        }
    }

    // puts the armies on the map, only on the territories of that player
    private void applyReinforce(final ReinforceEvent reinforce) {
        for (final Territory territory : reinforce.reinforcement().keySet()) {
            final int armies = reinforce.reinforcement().get(territory);
            if (armies > 0 && isOwnedBy(territory, reinforce.player())) {
                territory.addArmies(armies);
            }
        }
        publish(reinforce);
    }

    // the battle: dice, losses, maybe a conquest, and then everybody is told
    private void executeAttack(final AttackEvent attack) {
        final var source = attack.attackSource();
        final var target = attack.attackDestination();
        final var result = this.combat.resolve(source, target,
                attack.attackerStrength(), attack.defenderStrength());
        source.removeArmies(result.getAttackerLosses());
        target.removeArmies(result.getDefenderLosses());
        if (result.isConquered()) {
            conquer(attack, result);
        }
        publishBattle(attack, result);
        checkVictory(attack.attacker());
    }

    // the attacker takes the territory and moves in the armies that survived the battle
    private void conquer(final AttackEvent attack, final BattleResult result) {
        final int survivors = attack.attackerStrength() - result.getAttackerLosses();
        attack.attackDestination().setOwner(attack.attacker().getId());
        attack.attackSource().removeArmies(survivors);
        attack.attackDestination().addArmies(survivors);
        this.conquered = true;
        // who has no territories left is out of the game
        if (this.map.getTerritoriesOf(attack.defender().getId()).isEmpty()) {
            this.turn.remove(attack.defender());
            // hamail's check
            this.targetDestroyed = this.elimination.managePlayerElimination(attack.defender(), attack.attacker());
        }
    }

    // after an attack the attacker could have reached the objective,
    // and who has the whole map wins anyway, whatever the objective is
    private void checkVictory(final Player player) {
        final boolean wholeMap = this.map.getTerritoriesOf(player.getId()).size() == this.map.getTerritories().size();
        final boolean objectiveDone = this.targetDestroyed
                || player.getObjective() != null && this.victory.victoryCheck(player);
        if (wholeMap || objectiveDone) {
            this.turn.setWinner(player);
            if (this.view != null) {
                this.view.showGameOver(player);
            }
        }
    }

    // the armies go from one territory to the other one
    private void executeMove(final MoveEvent move) {
        move.sourceTerritory().removeArmies(move.troopsMoved());
        move.destinationTerritory().addArmies(move.troopsMoved());
        publish(move);
    }

    // the turn passes, then the bots play by themselves until a human has to play
    private void passTurn() {
        drawCard();
        this.turn.next();
        letBotsPlay();
    }

    // card if conquered
    private void drawCard() {
        if (this.conquered) {
            this.draw.drawNewCard();
        }
        this.conquered = false;
    }

    // the bots play their turns by themselves until it's the turn of a human
    private void letBotsPlay() {
        // at most one round, so with only bots the window doesn't get stuck
        int botTurns = 0;
        while (!this.turn.isGameOver() && !this.turn.getCurrentPlayer().isHuman()
                && botTurns < this.roster.getAllPlayers().size()) {
            playBotTurn();
            botTurns++;
            if (!this.turn.isGameOver()) {
                drawCard();
                this.turn.next();
            }
        }
    }

    // a bot plays its whole turn with its strategy: reinforce, some attacks and one move
    private void playBotTurn() {
        final var bot = this.turn.getCurrentPlayer();
        final var strategy = bot.getStrategy();
        applyReinforce(strategy.getReinforce(bot, reinforcementsOf(bot)));
        for (int i = 0; i < MAX_BOT_ATTACKS && !this.turn.isGameOver(); i++) {
            final var attack = strategy.getAttack(bot);
            if (attack.isEmpty() || !isValidAttack(attack.get())) {
                break;
            }
            executeAttack(attack.get());
        }
        final var move = strategy.getMove(bot);
        if (!this.turn.isGameOver() && move.isPresent() && isValidMove(move.get())) {
            executeMove(move.get());
        }
    }

    // the same rules of resolve, so a wrong attack of a bot doesn't crash the game
    private boolean isValidAttack(final AttackEvent attack) {
        final var source = attack.attackSource();
        final var target = attack.attackDestination();
        return isOwnedBy(source, attack.attacker())
                && isOwnedBy(target, attack.defender())
                && !isOwnedBy(target, attack.attacker())
                && source.getAdjacentIds().contains(target.getId())
                && attack.attackerStrength() >= 1
                && attack.attackerStrength() <= Dice.MAX_DICE
                && attack.attackerStrength() < source.getArmies()
                && attack.defenderStrength() >= 1
                && attack.defenderStrength() <= Math.min(Dice.MAX_DICE, target.getArmies());
    }

    // at least one army moves, one stays home, and the two territories are yours and connected
    private boolean isValidMove(final MoveEvent move) {
        final var source = move.sourceTerritory();
        final var destination = move.destinationTerritory();
        return !source.getId().equals(destination.getId())
                && move.troopsMoved() >= 1
                && move.troopsMoved() < source.getArmies()
                && this.map.areConnected(source.getId(), destination.getId(), move.player().getId());
    }

    // the territory belongs to that player
    private boolean isOwnedBy(final Territory territory, final Player player) {
        return territory.getOwnerId().equals(Optional.of(player.getId()));
    }

    private void setupAction() {}

}
