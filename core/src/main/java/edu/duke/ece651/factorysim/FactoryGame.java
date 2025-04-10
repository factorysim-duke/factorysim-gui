package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FactoryGame extends Game {
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture cellTexture;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        viewport.apply();
        camera.position.set(Constants.VIEW_WIDTH / 2f, Constants.VIEW_HEIGHT / 2f, 0);

        actor = new BuildingActor(new Texture("cell.png"));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Update camera
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);


        spriteBatch.begin();
        actor.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();

        cellTexture.dispose();
    }

    private void renderGrid() {

    }
}
