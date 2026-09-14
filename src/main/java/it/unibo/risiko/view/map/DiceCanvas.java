package it.unibo.risiko.view.map;

import it.unibo.risiko.model.battle.BattleResult;
import java.util.List;
import java.util.Optional;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Shows the dice of the last battle with the dots like on a real die.
 * It doesn't calculate anything, it just draws a {@link BattleResult}.
 */
public final class DiceCanvas extends Canvas {

    private static final double WIDTH = 300;
    private static final double HEIGHT = 110;
    private static final double SIDE = 44;
    private static final double GAP = 10;
    private static final double DOT = 7;
    private static final double DOT_MARGIN = 8;
    private static final double CORNER = 10;

    private static final double ATTACK_X = 14;
    private static final double DEFENCE_X = 170;
    private static final double DICE_Y = 12;
    private static final double LABELS_Y = 74;
    private static final double LOSSES_Y = 94;
    private static final double EMPTY_Y = 30;
    private static final double FONT_SIZE = 12;

    private static final int HIGHEST_FACE = 6;
    private static final int THREE = 3;

    private static final Color BACKGROUND = Color.rgb(240, 243, 244);
    private static final Color ATTACK_DIE = Color.rgb(190, 60, 45);
    private static final Color DEFENCE_DIE = Color.rgb(45, 85, 145);
    private static final Color TEXT = Color.rgb(60, 65, 70);

    private Optional<BattleResult> result = Optional.empty();

    /**
     * Creates the panel, still empty.
     */
    public DiceCanvas() {
        super(WIDTH, HEIGHT);
        redraw();
    }

    /**
     * Shows the dice of a battle.
     *
     * @param battleResult the result to show
     */
    public void setResult(final BattleResult battleResult) {
        this.result = Optional.of(battleResult);
        redraw();
    }

    /**
     * Empties the panel.
     */
    public void clear() {
        this.result = Optional.empty();
        redraw();
    }

    private void redraw() {
        final var context = getGraphicsContext2D();
        context.setFill(BACKGROUND);
        context.fillRect(0, 0, getWidth(), getHeight());
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.BASELINE);
        context.setFont(Font.font("SansSerif", FONT_SIZE));

        if (this.result.isEmpty()) {
            context.setFill(Color.GRAY);
            context.fillText("No battle yet", ATTACK_X, EMPTY_Y);
            return;
        }

        final var battleResult = this.result.get();
        drawRow(context, battleResult.getAttackerRolls(), ATTACK_X, ATTACK_DIE);
        drawRow(context, battleResult.getDefenderRolls(), DEFENCE_X, DEFENCE_DIE);

        context.setFill(TEXT);
        context.fillText("attack", ATTACK_X, LABELS_Y);
        context.fillText("defence", DEFENCE_X, LABELS_Y);
        context.setFont(Font.font("SansSerif", FontWeight.BOLD, FONT_SIZE));
        context.fillText("losses: attacker " + battleResult.getAttackerLosses()
                + ", defender " + battleResult.getDefenderLosses(), ATTACK_X, LOSSES_Y);
    }

    private void drawRow(final GraphicsContext context, final List<Integer> values,
                         final double x, final Color color) {
        double currentX = x;
        for (final Integer value : values) {
            context.setFill(color);
            context.fillRoundRect(currentX, DICE_Y, SIDE, SIDE, CORNER, CORNER);
            context.setStroke(color.darker());
            context.strokeRoundRect(currentX, DICE_Y, SIDE, SIDE, CORNER, CORNER);
            drawDots(context, currentX, value);
            currentX = currentX + SIDE + GAP;
        }
    }

    private void drawDots(final GraphicsContext context, final double x, final int value) {
        context.setFill(Color.WHITE);
        final double left = x + DOT_MARGIN;
        final double center = x + SIDE / 2 - DOT / 2;
        final double right = x + SIDE - DOT_MARGIN - DOT;
        final double top = DICE_Y + DOT_MARGIN;
        final double middle = DICE_Y + SIDE / 2 - DOT / 2;
        final double bottom = DICE_Y + SIDE - DOT_MARGIN - DOT;

        // odd numbers have the middle dot, the other dots come in pairs
        if (value % 2 != 0) {
            dot(context, center, middle);
        }
        if (value > 1) {
            dot(context, left, top);
            dot(context, right, bottom);
        }
        if (value > THREE) {
            dot(context, right, top);
            dot(context, left, bottom);
        }
        if (value == HIGHEST_FACE) {
            dot(context, left, middle);
            dot(context, right, middle);
        }
    }

    private void dot(final GraphicsContext context, final double x, final double y) {
        context.fillOval(x, y, DOT, DOT);
    }
}
