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
    private Grid grid;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        camera.position.set(0f, 0f, 0f);
        camera.update();
        viewport.apply();

        cellTexture = new Texture("cell.png");
        grid = new Grid(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, cellTexture);
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

        // Begin drawing
        spriteBatch.begin();

        // Draw the background grid
        grid.draw(spriteBatch);

        // End drawing
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();

        cellTexture.dispose();
    }
}
