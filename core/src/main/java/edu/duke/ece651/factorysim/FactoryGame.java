package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
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
        // Rendering
        spriteBatch = new SpriteBatch();

        // Set up camera & viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        camera.position.set(0f, 0f, 0f);
        camera.update();
        viewport.apply();

        // Set up input multiplexer
        Gdx.input.setInputProcessor(multiplexer);

        // Create screen
        SimulationScreen simulationScreen = new SimulationScreen(this);

        // Create game world
        int cols = Math.ceilDiv(Constants.VIEW_WIDTH, Constants.CELL_SIZE);
        int rows = Math.ceilDiv(Constants.VIEW_HEIGHT, Constants.CELL_SIZE);
        world = new GameWorld(cols, rows, Constants.CELL_SIZE, camera, viewport, new StreamLogger(System.out),
            simulationScreen, 0f, 0f);

        // Get simulation from game world
        sim = world.getSimulation();

        // Set screen as the screen of the game
        this.setScreen(simulationScreen);

        // Add world as an input processor
        // (make sure to do this after setting screen so that screen has higher input priority)
        addInputProcessor(world);

//        // TODO: Test code
//        world.loadFromJsonString(Gdx.files.internal("doors1.json").readString());
//
//        // TODO: Hardcode building info request, remove later
//        Building building = this.sim.getWorld().getBuildingFromName("D");
//        simulationScreen.showBuildingInfo(building);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    //load simulation from json file
    public void loadSimulation(String jsonPath) {
        this.sim = new Simulation(jsonPath);
        this.world.setSimulation(this.sim);
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
