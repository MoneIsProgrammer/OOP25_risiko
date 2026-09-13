package it.unibo.risiko.view.map;

import javafx.geometry.Point2D;

/**
 * Goes from the grid used in the map file to the pixels of the panel, which change every
 * time the window is resized. Drawing and clicking use the same one, so they never disagree.
 *
 * @param scale zoom factor, the same in both directions
 * @param offsetX pixels of margin on the left, to center the map
 * @param offsetY pixels of margin on top, to center the map
 */
record ViewTransform(double scale, double offsetX, double offsetY) {

    /**
     * Fits the whole grid in the given space without stretching it, and centers it.
     *
     * @param width available width in pixels
     * @param height available height in pixels
     * @return the corresponding transformation
     */
    static ViewTransform of(final double width, final double height) {
        final double scale = Math.min(width / MapLayout.LOGIC_WIDTH, height / MapLayout.LOGIC_HEIGHT);
        return new ViewTransform(
                scale,
                (width - MapLayout.LOGIC_WIDTH * scale) / 2,
                (height - MapLayout.LOGIC_HEIGHT * scale) / 2);
    }

    /**
     * False before the panel gets its size, when there is nothing to draw yet.
     *
     * @return true if there is space to draw
     */
    boolean usable() {
        return this.scale > 0;
    }

    /**
     * Converts an x on the grid into pixels.
     *
     * @param logic the x on the grid
     * @return the x in pixels
     */
    double screenX(final double logic) {
        return this.offsetX + logic * this.scale;
    }

    /**
     * Converts a y on the grid into pixels.
     *
     * @param logic the y on the grid
     * @return the y in pixels
     */
    double screenY(final double logic) {
        return this.offsetY + logic * this.scale;
    }

    /**
     * Converts a length on the grid into pixels.
     *
     * @param logic the length on the grid
     * @return the length in pixels
     */
    double length(final double logic) {
        return logic * this.scale;
    }

    /**
     * From pixels back to the grid: it's how we find out where the user clicked.
     *
     * @param x the x in pixels
     * @param y the y in pixels
     * @return the corresponding point on the logic grid
     */
    Point2D toLogic(final double x, final double y) {
        return new Point2D((x - this.offsetX) / this.scale, (y - this.offsetY) / this.scale);
    }
}
