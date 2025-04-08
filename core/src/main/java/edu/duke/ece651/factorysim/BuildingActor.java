package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildingActor extends Actor2D {
    private final Texture texture;

    public BuildingActor(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y);
    }
}
