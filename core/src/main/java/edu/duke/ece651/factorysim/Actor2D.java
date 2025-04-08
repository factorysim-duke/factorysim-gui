package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Actor2D {
    public final Vector2 position;

    public Actor2D(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public Actor2D() {
        this.position = Vector2.Zero;
    }

    public abstract void draw(SpriteBatch spriteBatch);
}
