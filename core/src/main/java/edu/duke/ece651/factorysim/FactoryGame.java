package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import java.util.*;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
import edu.duke.ece651.factorysim.util.PanelLogger;

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
    private Simulation sim;

    // Input
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    public void addInputProcessor(InputProcessor inputProcessor) {
        multiplexer.addProcessor(inputProcessor);
    }

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        camera.position.set(0f, 0f, 0f);
        camera.update();
        viewport.apply();

        Gdx.input.setInputProcessor(multiplexer);

        int cols = Math.ceilDiv(Constants.VIEW_WIDTH, Constants.CELL_SIZE);
        int rows = Math.ceilDiv(Constants.VIEW_HEIGHT, Constants.CELL_SIZE);
        world = new GameWorld(cols, rows, Constants.CELL_SIZE, camera, viewport, new StreamLogger(System.out),
            0f, 0f);
        multiplexer.addProcessor(world);

        sim = world.getSimulation();

        SimulationScreen simulationScreen = new SimulationScreen(this);
        this.setScreen(simulationScreen);

        // TODO: Test code
        world.loadSimulation(Gdx.files.internal("doors1.json").readString());

        // TODO: Hardcode building info request, remove later
        Building building = this.sim.getWorld().getBuildingFromName("D");
        simulationScreen.showBuildingInfo(building);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    //load simulation from json file
    public void loadSimulation(String jsonPath) {
        this.sim.load(jsonPath);
    }

    //save simulation to json file
    public void saveSimulation(String jsonPath) {
        this.sim.save(jsonPath);
    }

    //set logger
    public void setLogger(PanelLogger logger) {
        this.sim.setLogger(logger);
        this.world.setLogger(logger);
    }

    // set verbosity
    public void setVerbosity(int verbosity) {
        this.sim.setVerbosity(verbosity);
    }

    // make user request
    public void makeUserRequest(String itemName, String buildingName) {
        this.sim.makeUserRequest(itemName, buildingName);
    }

    //get current step
    public int getCurrentStep() {
        return this.sim.getCurrentTime();
    }

    //step simulation by n steps
    public void step(int n) {
        this.sim.step(n);
    }

    //set policy
    public void setPolicy(String type, String policy, String buildingName) {
        this.sim.setPolicy(type, policy, buildingName);
    }

    //finish simulation
    public void finish() {
        this.sim.finish();
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

        super.render();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        world.dispose();
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }
    }
}
