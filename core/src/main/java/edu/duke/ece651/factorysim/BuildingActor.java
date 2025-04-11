package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildingActor extends Actor2D {
    private final Building building;
    private final Texture texture;

    public Building getBuilding() { return this.building; }

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
