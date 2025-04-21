package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.*;
import edu.duke.ece651.factorysim.screen.ui.*;
import edu.duke.ece651.factorysim.screen.util.*;
import edu.duke.ece651.factorysim.screen.listeners.UIEventListenerFactory;

/**
 * This class is responsible for displaying the simulation screen and handling user input.
 * It initializes the UI components and attaches event listeners to them.
 */
public class SimulationScreen implements Screen {
    private GameWorld world;

    private Stage stage;
    private TopBar topBar;
    private LogPanel logPanel;
    private VisTable infoPanelContainer;
    private InfoPanel currentInfoPanel;
    private ControlPanel controlPanel;
    private BuildingButtonsPanel buildingButtonsPanel;
    private int currentStep = 0;
    private FileChooser createFileChooser;
    private FileChooser saveFileChooser;
    private RealTimeMenu realTimeMenu;

    // UI components
    private InfoPanelManager infoPanelManager;
    private RequestDialogManager requestDialogManager;
    private UIInitializer uiInitializer;
    private UIEventListenerFactory listenerFactory;

    // Textures for building buttons
    private Texture selectTexture;
    private Texture mineTexture;
    private Texture factoryTexture;
    private Texture storageTexture;

    @Override
    public void show() {
        // Create and use input multiplexer
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Create stage
        stage = new Stage(new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        inputMultiplexer.addProcessor(stage);

        // Create game world
        int cols = Math.ceilDiv(Constants.VIEW_WIDTH, Constants.CELL_SIZE);
        int rows = Math.ceilDiv(Constants.VIEW_HEIGHT, Constants.CELL_SIZE);
        this.world = new GameWorld(cols, rows, Constants.CELL_SIZE, new StreamLogger(System.out), this,
            0f, 0f);
        inputMultiplexer.addProcessor(this.world);

        // Load initial configuration from formula.json
        try {
            // This loads the formula.json which contains types and recipes
            loadSimulation("formula.json");
            System.out.println("Successfully loaded initial configuration from formula.json");
        } catch (Exception e) {
            System.out.println("Failed to load initial configuration: " + e.getMessage());
        }

        // Load textures first so they can be used in UI construction
        loadTextures();

        initializeUI();
        setupLayout();
        attachEventListeners();
    }

    /**
     * Load textures needed for the UI.
     */
    private void loadTextures() {
        // Use the existing textures from GameWorld
        selectTexture = new Texture("select.png");
        mineTexture = new Texture("icon_mine.png");
        factoryTexture = new Texture("icon_factory.png");
        storageTexture = new Texture("icon_storage.png");
    }

    /**
     * Initialize the UI components.
     */
    private void initializeUI() {
        // Initialize UI helper components
        uiInitializer = new UIInitializer();
        listenerFactory = new UIEventListenerFactory(this);
        infoPanelManager = new InfoPanelManager();
        requestDialogManager = new RequestDialogManager(this, stage);

        // Initialize UI components
        uiInitializer.initializeVisUI();
        uiInitializer.registerCustomStyles();

        // Create file choosers
        createFileChooser = FileDialogUtil.createFileChooser(this);
        saveFileChooser = FileDialogUtil.saveFileChooser(this);

        // Create UI panels
        topBar = new TopBar(currentStep);
        logPanel = new LogPanel();
        infoPanelContainer = new VisTable();
        infoPanelContainer.setVisible(false);
        controlPanel = new ControlPanel();
        realTimeMenu = new RealTimeMenu(this);

        // Create building buttons panel
        buildingButtonsPanel = new BuildingButtonsPanel(
            world,
            stage,
            selectTexture,
            mineTexture,
            factoryTexture,
            storageTexture
        );
        buildingButtonsPanel.setBackground(VisUI.getSkin().getDrawable("button"));
        buildingButtonsPanel.pad(10);

        // Set up logging
        world.setLogger(new PanelLogger(logPanel));
    }

    /**
     * Setup the layout of the simulation screen,
     * including adding the top bar, log panel, and info panel to the screen.
     */
    private void setupLayout() {
        // Create root layout
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Top bar
        root.add(topBar).colspan(3).expandX().fillX().pad(10).row();

        // Left panel (log panel)
        root.add(logPanel).width(200).height(500f).top().pad(10);

        // Center space
        root.add().expand().fill();

        // Right panel (info panel)
        VisTable rightCol = new VisTable();
        rightCol.top().padTop(10).padRight(10);
        rightCol.add(infoPanelContainer).top().width(220);
        root.add(rightCol).width(240).top().padTop(10).padRight(10).padBottom(10).expandY().fillY().row();

        // Bottom bar with building buttons in the middle
        VisTextButton realTimeButton = new VisTextButton("Real-time", "blue");
        realTimeButton.pad(5, 10, 5, 10);

        // Create a bottom row that has three sections: left, center, right
        root.add(realTimeButton).left().padLeft(65).padBottom(280);

        // Center section with building buttons
        root.add(buildingButtonsPanel).bottom().padBottom(10);

        // Right section with control panel
        root.add(controlPanel).bottom().right().pad(10);

        // Add real-time button listener
        realTimeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                realTimeMenu.updateButtonState();
                realTimeMenu.showMenu(stage, realTimeButton);
            }
        });
    }

    /**
     * Attach event listeners to the UI components.
     */
    private void attachEventListeners() {
        // Top bar listeners
        topBar.getLoadButton().addListener(listenerFactory.createLoadButtonListener(stage, createFileChooser, topBar));
        topBar.getSaveButton().addListener(listenerFactory.createSaveButtonListener(stage, saveFileChooser, topBar));

        // Log panel listeners
        logPanel.getVerbosityBox().addListener(listenerFactory.createVerbosityChangeListener(this));

        // Control panel listeners
        controlPanel.getStepButton().addListener(listenerFactory.createStepButtonListener(controlPanel, this));
        controlPanel.getFinishButton().addListener(listenerFactory.createFinishButtonListener(this, topBar));
    }

    /**
     * Show the building info panel.
     * @param building is the building to show the info panel for
     */
    public void showBuildingInfo(Building building) {
        currentInfoPanel = infoPanelManager.showBuildingInfo(building, infoPanelContainer, requestDialogManager);

        // Attach policy listeners - delegates to the InfoPanelManager
        infoPanelManager.attachPolicyListeners(currentInfoPanel, building, this);
    }

    /**
     * Hide the info panel.
     */
    public void hideInfoPanel() {
        infoPanelContainer.setVisible(false);
    }

    /**
     * Update the step count display.
     */
    public void updateStepCount() {
        currentStep = getCurrentStep();
        topBar.updateStepCount(currentStep);
    }

    /**
     * Render the simulation screen.
     * @param delta is the time since the last frame
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Update and render the world
        world.update(delta);
        world.render(delta);

        // Update step count display if it has changed (for real-time simulation)
        if (getCurrentStep() != currentStep) {
            updateStepCount();
        }

        stage.act(delta);
        stage.draw();
    }

    /**
     * Resize the simulation screen.
     * @param width is the width of the screen
     * @param height is the height of the screen
     */
    @Override
    public void resize(int width, int height) {
        world.resize(width, height);

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * Pause the simulation.
     */
    @Override
    public void pause() { }

    /**
     * Resume the simulation.
     */
    @Override
    public void resume() { }

    /**
     * Hide the simulation screen.
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Dispose of the simulation screen.
     */
    @Override
    public void dispose() {
        world.dispose();

        // Dispose textures
        selectTexture.dispose();
        mineTexture.dispose();
        factoryTexture.dispose();
        storageTexture.dispose();

        stage.dispose();
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }

    public void loadSimulation(String jsonPath) {
        Simulation sim = new Simulation(WorldBuilder.buildEmptyWorld(), 0, this.world.getLogger());
        sim.load(jsonPath);
        this.world.setSimulation(sim);
    }

    public void saveSimulation(String jsonPath) {
        this.world.getSim().save(jsonPath);
    }

    // set verbosity
    public void setVerbosity(int verbosity) {
        this.world.getSim().setVerbosity(verbosity);
    }

    // make user request
    public void makeUserRequest(String itemName, String buildingName) {
        try {
            this.world.getSim().makeUserRequest(itemName, buildingName);
        } catch (Exception e) {
            logPanel.appendLog("[ERROR] Failed to make user request: " + e.getMessage());
        }
    }

    //get current step
    public int getCurrentStep() {
        return this.world.getSim().getCurrentTime();
    }

    //step simulation by n steps
    public void step(int n) {
        // Stop real-time simulation if user choose to step manually
        if (this.world.isRealTimeEnabled()) {
            stopRealTimeSimulation();
        }
        this.world.step(n);
    }

    //set policy
    public void setPolicy(String type, String policy, String buildingName) {
        this.world.getSim().setPolicy(type, policy, buildingName);
    }

    //finish simulation
    public void finish() {
        // Stop real-time simulation if user choose to finish
        if (this.world.isRealTimeEnabled()) {
            stopRealTimeSimulation();
        }
        this.world.getSim().finish();
    }

    /**
     * Starts real-time simulation.
     */
    public void startRealTimeSimulation() {
        this.world.getRealTime().start();
        this.world.enableRealTime();
    }

    /**
     * Pauses real-time simulation.
     */
    public void pauseRealTimeSimulation() {
        if (this.world.isRealTimeEnabled()) {
            this.world.getRealTime().pause();
        }
    }

    /**
     * Resumes real-time simulation from a paused state.
     */
    public void resumeRealTimeSimulation() {
        if (this.world.isRealTimeEnabled()) {
            this.world.getRealTime().resume();
        }
    }

    /**
     * Stops real-time simulation.
     */
    public void stopRealTimeSimulation() {
        this.world.getRealTime().stop();
        this.world.disableRealTime();
    }

    /**
     * Sets the speed of real-time simulation.
     *
     * @param stepsPerSecond steps per second
     */
    public void setRealTimeSpeed(float stepsPerSecond) {
        this.world.getRealTime().setSpeed(stepsPerSecond);
    }

    /**
     * Gets the current real-time simulation speed.
     *
     * @return steps per second
     */
    public float getRealTimeSpeed() {
        return this.world.getRealTime().getSpeed();
    }

    /**
     * Checks if real-time simulation is enabled.
     *
     * @return true if enabled
     */
    public boolean isRealTimeEnabled() {
        return this.world.isRealTimeEnabled();
    }

    /**
     * Checks if real-time simulation is paused.
     *
     * @return true if paused
     */
    public boolean isRealTimePaused() {
        return this.world.getRealTime().isPaused();
    }
}
