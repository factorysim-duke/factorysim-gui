package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;

/**
 * Represents an actor used to manage a building entity.
 */
public class BuildingActor extends Actor2D {
    private final Building building;

    private final Animation<TextureRegion> animation;
    private float animationTimer;

    /**
     * Get the underlying `Building` instance used by this `BuildingActor`.
     *
     * @return the underlying `Building` instance used by this `BuildingActor`.
     */
    public Building getBuilding() { return this.building; }

    /**
     * Constructs an actor for a building with texture and position.
     *
     * @param building is the `Building` instance used by the actor.
     * @param animation is the animation used by the actor when rendering.
     * @param x is the x coordinate of the absolute position of the actor.
     * @param y is the y coordinate of the absolute position of the actor.
     */
    public BuildingActor(Building building, Animation<TextureRegion> animation, float x, float y) {
        super(x, y);
        this.building = building;
        this.animation = animation;
        this.animationTimer = 0f;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        animationTimer += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(animationTimer, true);
        spriteBatch.draw(currentFrame, position.x, position.y);
    }
}
