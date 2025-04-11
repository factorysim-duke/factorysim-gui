package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a grid entity.
 */
public class GridActor extends Actor2D implements MouseListener {
    private final int cols;
    private final int rows;
    private final int cellSize;

    private final Texture cellTexture;

    private final float unselectedFactor;

    private Float mouseX = null;
    private Float mouseY = null;

    /**
     * Get the number of columns in the grid.
     *
     * @return the number of columns in the grid
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in the grid.
     *
     * @return the number of rows in the grid
     */
    public int getRows() {
        return rows;
    }

    /**
     * Constructs a `GridActor` instance based on grid dimension, texture, and <b>bottom-left</b> absolute position of
     * the actor.
     *
     * @param cols is the number of cells horizontally.
     * @param rows is the number of cells vertically.
     * @param cellSize is the size of each cell.
     * @param cellTexture is the texture of each cell in the grid.
     * @param unselectedFactor is the multiplying factor applied to cell's RGB color when it's unselected.
     * @param x is the <b>bottom-left</b> x coordinate value of the actor's position.
     * @param y is the <b>bottom-left</b> y coordinate value of the actor's position.
     */
    public GridActor(int cols, int rows, int cellSize, Texture cellTexture, float unselectedFactor,
                     float x, float y) {
        super(x, y);
        this.cols = cols;
        this.rows = rows;
        this.cellSize = cellSize;
        this.cellTexture = cellTexture;
        this.unselectedFactor = unselectedFactor;
    }

    public GridActor(int cols, int rows, int cellSize, Texture cellTexture, float x, float y) {
        this(cols, rows, cellSize, cellTexture, 0.5f, x, y);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        Color originalColor = spriteBatch.getColor().cpy();
        Color darkerColor = originalColor.cpy().mul(unselectedFactor, unselectedFactor, unselectedFactor, 1f);

        // Get the cell the mouse is above
        float mouseX = this.mouseX == null ? 0f : this.mouseX;
        float mouseY = this.mouseY == null ? 0f : this.mouseY;
        int hoveredCol = (int)((mouseX - position.x) / cellSize);
        int hoveredRow = (int)((mouseY - position.y) / cellSize);

        // Draw each cell
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (col == hoveredCol && row == hoveredRow && this.mouseX != null && this.mouseY != null) {
                    spriteBatch.setColor(originalColor);
                } else {
                    spriteBatch.setColor(darkerColor);
                }

                float x = position.x + col * cellSize;
                float y = position.y + row * cellSize;
                spriteBatch.draw(cellTexture, x, y);
            }
        }

        spriteBatch.setColor(originalColor);
    }

    @Override
    public void onMouseMoved(float mouseX, float mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
