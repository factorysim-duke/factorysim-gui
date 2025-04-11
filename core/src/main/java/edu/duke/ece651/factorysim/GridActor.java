package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a grid entity.
 */
public class GridActor extends Actor2D {
    private final int cols;
    private final int rows;
    private final Texture cellTexture;

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
     * @param cellTexture is the texture of each cell in the grid.
     * @param x is the <b>bottom-left</b> x coordinate value of the actor's position.
     * @param y is the <b>bottom-left</b> y coordinate value of the actor's position.
     */
    public GridActor(int cols, int rows, Texture cellTexture, float x, float y) {
        super(x, y);
        this.cols = cols;
        this.rows = rows;
        this.cellTexture = cellTexture;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        int cellWidth = cellTexture.getWidth();
        int cellHeight = cellTexture.getHeight();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = position.x + col * cellWidth;
                float y = position.y + row * cellHeight;
                spriteBatch.draw(cellTexture, x, y);
            }
        }
    }
}
