package it.unibo.risiko.view.map;

import it.unibo.risiko.model.event.AttackEvent;
import it.unibo.risiko.model.event.AttackResultEvent;
import it.unibo.risiko.model.event.CardEvent;
import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.model.event.EventVisitor;
import it.unibo.risiko.model.event.MoveEvent;
import it.unibo.risiko.model.event.ReinforceEvent;
import it.unibo.risiko.model.map.Continent;
import it.unibo.risiko.model.map.GameMap;
import it.unibo.risiko.model.map.Territory;
import it.unibo.risiko.model.player.RisikoColors;
import it.unibo.risiko.utils.ColorConversion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Draws the map, continents as colored boxes, territories as pieces with the armies inside
 * and borders as lines. It only reads the {@link GameMap}, never changes it, and redraws
 * by itself when an event arrives. On a click it just says which territory was pressed.
 */
public final class MapCanvas extends Canvas implements MapView {

    /** Tells if an event changes the drawing. */
    private static final EventVisitor<Boolean> CHANGES_THE_DRAWING = new RedrawIfNeeded();

    private static final double RADIUS = 17;
    private static final double CONTINENT_MARGIN = 42;
    private static final double CLICK_TOLERANCE = 6;
    private static final double CONTINENT_CORNER = 26;

    private static final double SELECTION_WIDTH = 3.5;
    private static final double HIGHLIGHT_WIDTH = 3;
    private static final double BORDER_WIDTH = 1.4;
    private static final double ADJACENCY_WIDTH = 1.2;
    private static final double BOX_WIDTH = 1.5;

    private static final double FILL_OPACITY = 0.27;
    private static final double STROKE_OPACITY = 0.67;

    private static final double ARMIES_FONT = 15;
    private static final double NAME_FONT = 10;
    private static final double CONTINENT_FONT = 12;
    private static final double NAME_OFFSET = 13;
    private static final double TITLE_OFFSET_X = 10;
    private static final double TITLE_OFFSET_Y = 18;
    private static final double BRIGHTNESS_THRESHOLD = 0.55;

    private static final double RED_WEIGHT = 0.299;
    private static final double GREEN_WEIGHT = 0.587;
    private static final double BLUE_WEIGHT = 0.114;

    private static final Color BACKGROUND = Color.rgb(232, 238, 240);
    private static final Color ADJACENCY_LINE = Color.rgb(150, 165, 172);
    private static final Color NO_OWNER = Color.rgb(200, 205, 208);
    private static final Color BORDER = Color.rgb(40, 45, 50);
    private static final Color HIGHLIGHT = Color.rgb(215, 60, 45);

    private final GameMap map;
    private final MapLayout layout;
    private final Map<String, Color> playerColors = new HashMap<>();
    private final List<TerritoryClickListener> listeners = new ArrayList<>();
    private final Set<String> highlighted = new HashSet<>();

    private String selected;

    /**
     * Creates the map panel.
     *
     * @param map the map to draw, it is only read
     * @param layout the coordinates and the colors to draw it with
     */
    public MapCanvas(final GameMap map, final MapLayout layout) {
        super(MapLayout.LOGIC_WIDTH, MapLayout.LOGIC_HEIGHT);
        this.map = map;
        this.layout = layout;
        widthProperty().addListener((obs, oldV, newV) -> redraw());
        heightProperty().addListener((obs, oldV, newV) -> redraw());
        setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));
        setOnMouseMoved(e -> setCursor(
                territoryUnder(e.getX(), e.getY()).isPresent()
                        ? Cursor.HAND
                        : Cursor.DEFAULT));
    }

    @Override
    public void setPlayerColor(final String playerId, final RisikoColors color) {
        this.playerColors.put(playerId, ColorConversion.toJavaFxColor(color));
        redraw();
    }

    @Override
    public void setSelected(final String territoryId) {
        //throws if the territory doesn't exist
        this.selected = this.map.getTerritory(territoryId).getId();
        redraw();
    }

    @Override
    public Optional<String> getSelected() {
        return Optional.ofNullable(this.selected);
    }

    @Override
    public void clearSelection() {
        this.selected = null;
        redraw();
    }

    @Override
    public void setHighlighted(final Set<String> territoryIds) {
        this.highlighted.clear();
        this.highlighted.addAll(territoryIds);
        redraw();
    }

    @Override
    public void addTerritoryClickListener(final TerritoryClickListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void redraw() {
        final var context = getGraphicsContext2D();
        context.setFill(BACKGROUND);
        context.fillRect(0, 0, getWidth(), getHeight());

        final var transform = ViewTransform.of(getWidth(), getHeight());
        if (!transform.usable()) {
            return;
        }
        drawContinents(context, transform);
        drawAdjacencies(context, transform);
        drawTerritories(context, transform);
    }

    @Override
    public void onEvent(final Event event) {
        if (event.accept(CHANGES_THE_DRAWING)) {
            //move is over, remove the highlight
            this.highlighted.clear();
            this.selected = null;
            redraw();
        }
    }

    /**
     * Says if the map has to be redrawn after an event.
     */
    private static final class RedrawIfNeeded implements EventVisitor<Boolean> {

        @Override
        public Boolean visit(final AttackEvent event) {
            //just the intention, nothing changed on the map yet
            return false;
        }

        @Override
        public Boolean visit(final AttackResultEvent event) {
            return true;
        }

        @Override
        public Boolean visit(final MoveEvent event) {
            return true;
        }

        @Override
        public Boolean visit(final ReinforceEvent event) {
            return true;
        }

        @Override
        public Boolean visit(final CardEvent event) {
            //the armies arrive later with the reinforcement
            return false;
        }
    }

    private void drawContinents(final GraphicsContext context, final ViewTransform transform) {
        context.setLineWidth(BOX_WIDTH);
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.BASELINE);
        context.setFont(font(CONTINENT_FONT, transform));

        for (final Continent continent : this.map.getContinents()) {
            final var points = new ArrayList<Point2D>();
            for (final String id : continent.getTerritoryIds()) {
                if (this.layout.hasPosition(id)) {
                    points.add(this.layout.getPosition(id));
                }
            }
            if (points.isEmpty()) {
                continue;
            }

            //box around all the territories of the continent
            double minX = points.get(0).getX();
            double minY = points.get(0).getY();
            double maxX = minX;
            double maxY = minY;
            for (final Point2D point : points) {
                minX = Math.min(minX, point.getX());
                minY = Math.min(minY, point.getY());
                maxX = Math.max(maxX, point.getX());
                maxY = Math.max(maxY, point.getY());
            }

            final double x = transform.screenX(minX - CONTINENT_MARGIN);
            final double y = transform.screenY(minY - CONTINENT_MARGIN);
            final double width = transform.length(maxX - minX + 2 * CONTINENT_MARGIN);
            final double height = transform.length(maxY - minY + 2 * CONTINENT_MARGIN);
            final double corner = transform.length(CONTINENT_CORNER);

            final Color base = this.layout.getContinentColor(continent.getId());
            context.setFill(base.deriveColor(0, 1, 1, FILL_OPACITY));
            context.fillRoundRect(x, y, width, height, corner, corner);
            context.setStroke(base.deriveColor(0, 1, 1, STROKE_OPACITY));
            context.strokeRoundRect(x, y, width, height, corner, corner);

            context.setFill(base.darker().darker());
            context.fillText(continent.getName() + "  +" + continent.getBonusArmies(),
                    x + transform.length(TITLE_OFFSET_X),
                    y + transform.length(TITLE_OFFSET_Y));
        }
    }

    private void drawAdjacencies(final GraphicsContext context, final ViewTransform transform) {
        context.setStroke(ADJACENCY_LINE);
        context.setLineWidth(ADJACENCY_WIDTH);
        for (final Territory territory : this.map.getTerritories()) {
            if (!this.layout.hasPosition(territory.getId())) {
                continue;
            }
            final Point2D from = this.layout.getPosition(territory.getId());
            for (final String neighbour : territory.getAdjacentIds()) {
                //every line is drawn only once
                if (territory.getId().compareTo(neighbour) >= 0 || !this.layout.hasPosition(neighbour)) {
                    continue;
                }
                final Point2D to = this.layout.getPosition(neighbour);
                context.strokeLine(
                        transform.screenX(from.getX()), transform.screenY(from.getY()),
                        transform.screenX(to.getX()), transform.screenY(to.getY()));
            }
        }
    }

    private void drawTerritories(final GraphicsContext context, final ViewTransform transform) {
        final double radius = transform.length(RADIUS);
        final Font armiesFont = font(ARMIES_FONT, transform);
        final Font nameFont = font(NAME_FONT, transform);
        context.setTextAlign(TextAlignment.CENTER);

        for (final Territory territory : this.map.getTerritories()) {
            if (!this.layout.hasPosition(territory.getId())) {
                continue;
            }
            final var position = this.layout.getPosition(territory.getId());
            final double x = transform.screenX(position.getX());
            final double y = transform.screenY(position.getY());

            final Color color = colorOf(territory);
            context.setFill(color);
            context.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            if (territory.getId().equals(this.selected)) {
                context.setStroke(BORDER);
                context.setLineWidth(SELECTION_WIDTH);
            } else if (this.highlighted.contains(territory.getId())) {
                context.setStroke(HIGHLIGHT);
                context.setLineWidth(HIGHLIGHT_WIDTH);
            } else {
                context.setStroke(BORDER);
                context.setLineWidth(BORDER_WIDTH);
            }
            context.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

            //armies in the middle
            context.setFont(armiesFont);
            context.setTextBaseline(VPos.CENTER);
            context.setFill(readableTextOn(color));
            context.fillText(String.valueOf(territory.getArmies()), x, y);

            //name under it
            context.setFont(nameFont);
            context.setTextBaseline(VPos.TOP);
            context.setFill(BORDER);
            context.fillText(territory.getName(), x, y + radius + transform.length(NAME_OFFSET));
        }
    }

    private Font font(final double logicSize, final ViewTransform transform) {
        return Font.font("SansSerif", FontWeight.BOLD, Math.max(1, transform.length(logicSize)));
    }

    private Color colorOf(final Territory territory) {
        return territory.getOwnerId()
                .map(a -> this.playerColors.getOrDefault(a, NO_OWNER))
                .orElse(NO_OWNER);
    }

    private Color readableTextOn(final Color background) {
        final double brightness = RED_WEIGHT * background.getRed()
                + GREEN_WEIGHT * background.getGreen()
                + BLUE_WEIGHT * background.getBlue();
        return brightness < BRIGHTNESS_THRESHOLD ? Color.WHITE : BORDER;
    }

    private void handleClick(final double x, final double y) {
        final var hit = territoryUnder(x, y);
        if (hit.isPresent()) {
            this.listeners.forEach(a -> a.onTerritoryClicked(hit.get()));
        } else {
            this.listeners.forEach(TerritoryClickListener::onEmptyClicked);
        }
    }

    /**
     * Finds the territory under the pointer, if two are close the nearest one wins.
     *
     * @param x x of the pointer in pixels
     * @param y y of the pointer in pixels
     * @return the id of the territory that was pressed, empty if the click hit nothing
     */
    private Optional<String> territoryUnder(final double x, final double y) {
        final var transform = ViewTransform.of(getWidth(), getHeight());
        if (!transform.usable()) {
            return Optional.empty();
        }
        final var point = transform.toLogic(x, y);
        final double grabRadius = RADIUS + CLICK_TOLERANCE / transform.scale();

        String closest = null;
        double closestDistance = 0;
        for (final Territory territory : this.map.getTerritories()) {
            if (!this.layout.hasPosition(territory.getId())) {
                continue;
            }
            final double distance = this.layout.getPosition(territory.getId()).distance(point);
            if (distance <= grabRadius && (closest == null || distance < closestDistance)) {
                closest = territory.getId();
                closestDistance = distance;
            }
        }
        return Optional.ofNullable(closest);
    }
}
