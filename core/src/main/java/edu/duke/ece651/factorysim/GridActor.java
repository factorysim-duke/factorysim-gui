package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a grid entity.
 */
public class GridActor extends Actor2D implements MouseListener {
    private final int cols;
    private final int rows;
    private final int cellSize;

    private final Texture cellTexture;
    private Texture selectTexture;

    private Vector2 mousePos = null;

    /**
     * Get the number of columns in the grid.
     *
     * @return the number of columns in the grid
     */
    public int getCols() { return this.cols; }

    /**
     * Get the number of rows in the grid.
     *
     * @return the number of rows in the grid
     */
    public int getRows() { return this.rows; }

    /**
     * Gets the reference of the current texture of the selection box.
     *
     * @return the reference of the current texture of the selection box.
     */
    public Texture getSelectTexture() { return this.selectTexture; }

    /**
     * Sets a new texture for the selection box.
     *
     * @param t the new selection box texture.
     */
    public void setSelectTexture(Texture t) { this.selectTexture = t; }

    /**
     * Constructs a `GridActor` instance based on grid dimension, texture, and <b>bottom-left</b> absolute position of
     * the actor.
     *
     * @param cols is the number of cells horizontally.
     * @param rows is the number of cells vertically.
     * @param cellSize is the size of each cell.
     * @param cellTexture is the texture of each cell in the grid.
     * @param selectTexture is the texture of the selection box.
     * @param x is the <b>bottom-left</b> x coordinate value of the actor's position.
     * @param y is the <b>bottom-left</b> y coordinate value of the actor's position.
     */
    public GridActor(int cols, int rows, int cellSize,
                     Texture cellTexture, Texture selectTexture,
                     float x, float y) {
        super(x, y);
        this.cols = cols;
        this.rows = rows;
        this.cellSize = cellSize;
        this.cellTexture = cellTexture;
        this.selectTexture = selectTexture;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        // Draw the grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = position.x + col * cellSize;
                float y = position.y + row * cellSize;
                spriteBatch.draw(cellTexture, x, y);
            }
        }
    }

    /**
     * Draw the selection box based on previously updated mouse position.
     *
     * @param spriteBatch is the `SpriteBatch` instance used to draw.
     */
    public void drawSelectionBox(SpriteBatch spriteBatch) {
        // Draw selection box
        if (mousePos != null) {
            int col = (int)((mousePos.x - position.x) / cellSize);
            int row = (int)((mousePos.y - position.y) / cellSize);
            if (col >= 0 && col < cols && row >= 0 && row < rows) {
                spriteBatch.draw(selectTexture, position.x + col * cellSize, position.y + row * cellSize);
            }
        }
    }

    @Override
    public void onMouseMoved(float mouseX, float mouseY) {
        if (mousePos == null) {
            mousePos = new Vector2(mouseX, mouseY);
            return;
        }
        mousePos.set(mouseX, mouseY);
    }
}
