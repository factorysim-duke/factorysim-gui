package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import java.util.*;

/**
 * Represents a libGDX game application of factorysim.
 */
public class FactoryGame extends Game {
    // Rendering
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Game World
    private GameWorld world;

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
        world = new GameWorld(cols, rows, Constants.CELL_SIZE, camera, viewport, 0f, 0f);
        Gdx.input.setInputProcessor(world);

        // TODO: Delete test code
        BuildingActor mine = world.buildMine("M", new Recipe(new Item("metal"), new HashMap<>(), 1),
            new Coordinate(5, 5));
        BuildingActor factory = world.buildFactory("Hi", new Type("Hi", List.of()),
            new Coordinate(20, 20));
        BuildingActor storage = world.buildStorage("St", new Item("metal"), 10, 1.0,
            new Coordinate(30, 10));
//        world.connectPath(mine, factory);
//        world.connectPath(factory, storage);
//        world.connectPath(storage, factory);
//        world.connectPath(factory, mine);

        world.loadSimulation(Gdx.files.internal("doors1.json").readString());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Update camera
        viewport.apply();
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Render the world
        spriteBatch.begin();
        world.update(spriteBatch, Gdx.graphics.getDeltaTime());
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
    }
}
