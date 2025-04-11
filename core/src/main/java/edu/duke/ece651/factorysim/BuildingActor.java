package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents an actor used to manage a building entity.
 */
public class BuildingActor extends Actor2D {
    private final Building building;
    private final Texture texture;

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
     * @param texture is the texture of the actor to be rendered.
     * @param x is the x coordinate of the absolute position of the actor.
     * @param y is the y coordinate of the absolute position of the actor.
     */
    public BuildingActor(Building building, Texture texture, float x, float y) {
        super(x, y);
        this.building = building;
        this.texture = texture;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y);
    }
}
