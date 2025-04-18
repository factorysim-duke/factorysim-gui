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
    private Logger logger = new StreamLogger(System.out);

    // Real-time simulation
    private RealTimeSimulation realTimeSimulation;
    private boolean realTimeEnabled = false;

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
        world = new GameWorld(cols, rows, Constants.CELL_SIZE, camera, viewport, logger, simulationScreen,
            0f, 0f);

        // Get simulation from game world
        sim = world.getSimulation();

        // Initialize real-time simulation handler
        realTimeSimulation = new RealTimeSimulation(sim);

        // Set screen as the screen of the game
        this.setScreen(simulationScreen);

        // Add world as an input processor
        // (make sure to do this after setting screen so that screen has higher input priority)
        addInputProcessor(world);

          // TODO: Test code
//        world.loadFromJsonString(Gdx.files.internal("doors1.json").readString());
//
//        // TODO: Hardcode building info request, remove later
//        Building building = this.sim.getWorld().getBuildingFromName("D");
//        simulationScreen.showBuildingInfo(building);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        if (getScreen() instanceof SimulationScreen) {
            ((SimulationScreen) getScreen()).resize(width, height);
        }
    }

    //load simulation from json file
    public void loadSimulation(String jsonPath) {
        this.sim = new Simulation(jsonPath);
        this.sim.setLogger(this.logger);
        this.world.setSimulation(this.sim);
        this.realTimeSimulation = new RealTimeSimulation(this.sim);
    }

    //save simulation to json file
    public void saveSimulation(String jsonPath) {
        this.sim.save(jsonPath);
    }

    //set logger
    public void setLogger(PanelLogger logger) {
        this.logger = logger;
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
        // Stop real-time simulation if user choose to step manually
        if (realTimeEnabled) {
            stopRealTimeSimulation();
        }
        this.sim.step(n);
    }

    //set policy
    public void setPolicy(String type, String policy, String buildingName) {
        this.sim.setPolicy(type, policy, buildingName);
    }

    //finish simulation
    public void finish() {
        // Stop real-time simulation if user choose to finish
        if (realTimeEnabled) {
            stopRealTimeSimulation();
        }
        this.sim.finish();
    }

    /**
     * Starts real-time simulation.
     */
    public void startRealTimeSimulation() {
        realTimeSimulation.start();
        realTimeEnabled = true;
    }

    /**
     * Pauses real-time simulation.
     */
    public void pauseRealTimeSimulation() {
        if (realTimeEnabled) {
            realTimeSimulation.pause();
        }
    }

    /**
     * Resumes real-time simulation from a paused state.
     */
    public void resumeRealTimeSimulation() {
        if (realTimeEnabled) {
            realTimeSimulation.resume();
        }
    }

    /**
     * Stops real-time simulation.
     */
    public void stopRealTimeSimulation() {
        realTimeSimulation.stop();
        realTimeEnabled = false;
    }

    /**
     * Sets the speed of real-time simulation.
     *
     * @param stepsPerSecond steps per second
     */
    public void setRealTimeSpeed(float stepsPerSecond) {
        realTimeSimulation.setSpeed(stepsPerSecond);
    }

    /**
     * Gets the current real-time simulation speed.
     *
     * @return steps per second
     */
    public float getRealTimeSpeed() {
        return realTimeSimulation.getSpeed();
    }

    /**
     * Checks if real-time simulation is enabled.
     *
     * @return true if enabled
     */
    public boolean isRealTimeEnabled() {
        return realTimeEnabled;
    }

    /**
     * Checks if real-time simulation is paused.
     *
     * @return true if paused
     */
    public boolean isRealTimePaused() {
        return realTimeSimulation.isPaused();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Update real-time simulation
        if (realTimeEnabled) {
            int stepsExecuted = realTimeSimulation.update(Gdx.graphics.getDeltaTime());

            // Log steps executed for debug
            // if (stepsExecuted > 0) {
            //     logger.log(0, "Executed " + stepsExecuted + " steps in real-time");
            // }
        }

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
        // Stop real-time simulation before disposing
        if (realTimeEnabled) {
            stopRealTimeSimulation();
        }

        spriteBatch.dispose();
        world.dispose();
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }
    }
}
