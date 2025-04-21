package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a grid entity.
 */
public class GridActor extends Actor2D {
    private int cols;
    private int rows;
    private final int cellSize;

    private final Texture cellTexture;
    private Texture selectTexture;

    private final Color selectColor = Color.WHITE.cpy();

    private final Vector2 mousePos = new Vector2(0f, 0f);

    /**
     * Gets the number of columns in the grid.
     *
     * @return the number of columns in the grid
     */
    public int getCols() { return this.cols; }

    /**
     * Gets the number of rows in the grid.
     *
     * @return the number of rows in the grid
     */
    public int getRows() { return this.rows; }

    /**
     * Calculates the width of the grid.
     *
     * @return the width of the grid.
     */
    public int getWidth() { return cols * cellSize; }

    /**
     * Calculates the height of the grid.
     *
     * @return the height of the grid.
     */
    public int getHeight() { return rows * cellSize; }

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
     * Sets a new color for the selection box.
     *
     * @param c the new selection box color.
     */
    public void setSelectColor(Color c) { this.selectColor.set(c); }

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

    public void drawGrid(SpriteBatch spriteBatch) {
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
        // Record original color
        float r = spriteBatch.getColor().r;
        float g = spriteBatch.getColor().g;
        float b = spriteBatch.getColor().b;
        float a = spriteBatch.getColor().a;

        // Set to selection color
        spriteBatch.setColor(selectColor.r, selectColor.g, selectColor.b, selectColor.a);

        // Draw selection box
        int col = (int)((mousePos.x - position.x) / cellSize);
        int row = (int)((mousePos.y - position.y) / cellSize);
        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            spriteBatch.draw(selectTexture, position.x + col * cellSize, position.y + row * cellSize);
        }

        // Resume original color
        spriteBatch.setColor(r, g, b, a);
    }

    public void resize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void onMouseMoved(Vector2 mousePos) {
        this.mousePos.set(mousePos);
    }
}
