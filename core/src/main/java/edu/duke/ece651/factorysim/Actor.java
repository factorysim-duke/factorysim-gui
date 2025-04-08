package edu.duke.ece651.factorysim;

import com.badlogic.gdx.math.Vector2;

public class Actor {
    public final Vector2 position;

    public Actor(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public Actor() {
        this.position = Vector2.Zero;
    }
}
