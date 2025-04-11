package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FactoryGame extends Game {
    // Rendering
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Game World
    private WorldActor world;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        camera.position.set(0f, 0f, 0f);
        camera.update();
        viewport.apply();

        int cols = Math.ceilDiv(Constants.VIEW_WIDTH, Constants.CELL_SIZE);
        int rows = Math.ceilDiv(Constants.VIEW_HEIGHT, Constants.CELL_SIZE);
        world = new WorldActor(cols, rows, Constants.CELL_SIZE, 0f, 0f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Update camera
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Draw the world
        spriteBatch.begin();
        world.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
    }
}
