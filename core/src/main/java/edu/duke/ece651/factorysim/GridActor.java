package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GridActor extends Actor2D {
    private final int cols;
    private final int rows;
    private final Texture cellTexture;

    /**
     *
     * @param cols
     * @param rows
     * @param cellTexture
     * @param x is the <b>bottom-left</b> x coordinate value of the actor's position.
     * @param y is the <b>bottom-left</b> y coordinate value of the actor's position.
     */
    public GridActor(int cols, int rows, Texture cellTexture, float x, float y) {
        super(x, y);
        this.cols = cols;
        this.rows = rows;
        this.cellTexture = cellTexture;
    }

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
