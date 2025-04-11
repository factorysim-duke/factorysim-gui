package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GridActor extends Actor2D {
    private final int width;
    private final int height;
    private final Texture cellTexture;

    public GridActor(float x, float y, int width, int height, Texture cellTexture) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.cellTexture = cellTexture;
    }

    public GridActor(int width, int height, Texture cellTexture) {
        this(0f, 0f, width, height, cellTexture);
    }

    public void draw(SpriteBatch spriteBatch) {
        int cellWidth = cellTexture.getWidth();
        int cellHeight = cellTexture.getHeight();
        for (int y = -height; y < height; y += cellHeight) {
            for (int x = -width; x < width; x += cellWidth) {
                spriteBatch.draw(cellTexture, position.x + x, position.y + y);
            }
        }
    }
}
