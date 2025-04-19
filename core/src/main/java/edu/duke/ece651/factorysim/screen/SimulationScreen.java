// Base SimulationScreen class with reduced responsibilities
package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.screen.ui.ControlPanel;
import edu.duke.ece651.factorysim.screen.ui.InfoPanel;
import edu.duke.ece651.factorysim.screen.ui.InfoPanelManager;
import edu.duke.ece651.factorysim.screen.ui.LogPanel;
import edu.duke.ece651.factorysim.screen.ui.RealTimeMenu;
import edu.duke.ece651.factorysim.screen.ui.RequestDialogManager;
import edu.duke.ece651.factorysim.screen.ui.TopBar;
import edu.duke.ece651.factorysim.screen.ui.UIInitializer;
import edu.duke.ece651.factorysim.screen.util.FileDialogUtil;
import edu.duke.ece651.factorysim.screen.util.PanelLogger;
import edu.duke.ece651.factorysim.screen.listeners.UIEventListenerFactory;

/**
 * This class is responsible for displaying the simulation screen and handling user input.
 * It initializes the UI components and attaches event listeners to them.
 */
public class SimulationScreen implements Screen {
    private Stage stage;
    private final FactoryGame game;
    private TopBar topBar;
    private LogPanel logPanel;
    private VisTable infoPanelContainer;
    private InfoPanel currentInfoPanel;
    private ControlPanel controlPanel;
    private int currentStep = 0;
    private FileChooser createFileChooser;
    private FileChooser saveFileChooser;
    private RealTimeMenu realTimeMenu;

    private InfoPanelManager infoPanelManager;
    private RequestDialogManager requestDialogManager;
    private UIInitializer uiInitializer;
    private UIEventListenerFactory listenerFactory;

    /**
     * Constructor for the SimulationScreen class.
     * @param game is the FactoryGame instance
     */
    public SimulationScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1600, 900));
        game.addInputProcessor(stage);

        initializeUI();
        setupLayout();
        attachEventListeners();
    }

    /**
     * Initialize the UI components.
     */
    private void initializeUI() {
        // Initialize UI helper components
        uiInitializer = new UIInitializer();
        listenerFactory = new UIEventListenerFactory(game);
        infoPanelManager = new InfoPanelManager();
        requestDialogManager = new RequestDialogManager(game, stage);

        // Initialize UI components
        uiInitializer.initializeVisUI();
        uiInitializer.registerCustomStyles();

        // Create file choosers
        createFileChooser = FileDialogUtil.createFileChooser(game);
        saveFileChooser = FileDialogUtil.saveFileChooser(game);

        // Create UI panels
        topBar = new TopBar(currentStep);
        logPanel = new LogPanel();
        infoPanelContainer = new VisTable();
        infoPanelContainer.setVisible(false);
        controlPanel = new ControlPanel();
        realTimeMenu = new RealTimeMenu(game);

        // Set up logging
        game.setLogger(new PanelLogger(logPanel));
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

        // Bottom bar
        VisTextButton realTimeButton = new VisTextButton("Real-time", "blue");
        realTimeButton.pad(5, 10, 5, 10);
        root.add(realTimeButton).left().padLeft(65).padBottom(280);
        root.add().expand();
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
        logPanel.getVerbosityBox().addListener(listenerFactory.createVerbosityChangeListener(game));

        // Control panel listeners
        controlPanel.getStepButton().addListener(listenerFactory.createStepButtonListener(game, controlPanel, this));
        controlPanel.getFinishButton().addListener(listenerFactory.createFinishButtonListener(game, topBar));
    }

    /**
     * Show the building info panel.
     * @param building is the building to show the info panel for
     */
    public void showBuildingInfo(Building building) {
        currentInfoPanel = infoPanelManager.showBuildingInfo(building, infoPanelContainer, requestDialogManager);

        // Attach policy listeners - delegates to the InfoPanelManager
        infoPanelManager.attachPolicyListeners(currentInfoPanel, building, game);
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
        currentStep = game.getCurrentStep();
        topBar.updateStepCount(currentStep);
    }

    /**
     * Render the simulation screen.
     * @param delta is the time since the last frame
     */
    @Override
    public void render(float delta) {
        // Update step count display if it has changed (for real-time simulation)
        if (game.getCurrentStep() != currentStep) {
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
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * Pause the simulation.
     */
    @Override
    public void pause() {}

    /**
     * Resume the simulation.
     */
    @Override
    public void resume() {}

    /**
     * Hide the simulation screen.
     */
    @Override
    public void hide() {
        dispose();
    }

    /**
     * Dispose of the simulation screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }
}