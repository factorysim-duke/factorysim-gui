package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents an abstract 2D entity with an absolute position and the ability to be rendered by a `SpriteBatch`.
 */
public abstract class Actor2D {
    public final Vector2 position;

    /**
     * Constructs an actor with a given absolute position.
     *
     * @param x is the x coordinate of the absolute position of the actor.
     * @param y is the y coordinate of the absolute position of the actor.
     */
    public Actor2D(float x, float y) {
        this.position = new Vector2(x, y);
    }

    /**
     * Uses a `SpriteBatch` instance to render the 2D actor.
     *
     * @param spriteBatch is the `SpriteBatch` instance used to render this actor.
     */
    public abstract void draw(SpriteBatch spriteBatch);
}
