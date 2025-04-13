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

    //TODO: double check how to work with gameworld
    //constructor
    public FactoryGame() {
        super();
        this.sim = new Simulation("doors1.json");
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
        this.logger = logger;
        this.sim.setLogger(this.logger);
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
    public void create() {
        SimulationScreen simulationScreen = new SimulationScreen(this);
        this.setScreen(simulationScreen);
        // TODO: Hardcode building info request, remove later
        Building building = this.sim.getWorld().getBuildingFromName("D");
        simulationScreen.showBuildingInfo(building);
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
