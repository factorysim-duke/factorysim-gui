package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;

/**
 * Represents an actor used to manage a building entity.
 */
public class BuildingActor extends Actor2D {
    private final Building building;

    private final Animator<TextureRegion> animator;

    private final Texture removeTexture;

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
     * @param removeTexture is the texture used to draw over a building to indicate it's pending to be removed.
     * @param x is the x coordinate of the absolute position of the actor.
     * @param y is the y coordinate of the absolute position of the actor.
     */
    public BuildingActor(Building building, Animation<TextureRegion> animation, Texture removeTexture,
                         float x, float y) {
        super(x, y);
        this.building = building;
        this.animator = new Animator<>(animation, true);
        this.removeTexture = removeTexture;
    }

    public void draw(SpriteBatch spriteBatch) {
        animator.step(Gdx.graphics.getDeltaTime()); // Update the animator
        spriteBatch.draw(animator.getCurrentKeyFrame(), position.x, position.y);
        if (building.isPendingRemoval()) {
            spriteBatch.draw(removeTexture, position.x, position.y);
        }
    }
}
