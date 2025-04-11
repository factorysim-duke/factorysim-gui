package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.*;

// TODO
public class PathActor extends Actor2D {
    private final Animator<TextureRegion> animator;

    public PathActor(float x, float y, Animator<TextureRegion> animator) {
        super(x, y);
        this.animator = animator;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(animator.getCurrentKeyFrame(), position.x, position.y);
    }
}
