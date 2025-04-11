package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildingActor extends Actor2D {
    private final Texture texture;

    public BuildingActor(float x, float y, Texture texture) {
        super(x, y);
        this.texture = texture;
    }

    public BuildingActor(Texture texture) {
        super();
        this.texture = texture;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y);
    }
}
