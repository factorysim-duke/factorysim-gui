package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.screen.SimulationScreen;

/**
 * Menu for real-time simulation controls.
 */
public class RealTimeMenu extends VisWindow {
    private VisTextButton startPauseButton;
    private VisTextField stepsPerSecondField;
    private final SimulationScreen screen;

    /**
     * Creates a real-time controls menu.
     *
     * @param screen the simulation screen instance
     */
    public RealTimeMenu(final SimulationScreen screen) {
        super("Real Time Controls");
        this.screen = screen;

        // Remove default window decorations
        getTitleLabel().setVisible(false);
        getTitleTable().clear();
        setMovable(false);
        setResizable(false);

        // Set window style
        pad(10);

        // Start/Pause button
        startPauseButton = new VisTextButton("Start", "orange");

        // Steps per second label and field
        VisLabel stepsPerSecondLabel = new VisLabel("Steps per second: ");
        stepsPerSecondLabel.setColor(Color.WHITE);
        stepsPerSecondField = new VisTextField("5");
        stepsPerSecondField.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

        // Add components to the menu
        add(startPauseButton).fillX().expandX().padBottom(10).row();

        VisTable controlsTable = new VisTable();
        controlsTable.add(stepsPerSecondLabel).left();
        controlsTable.add(stepsPerSecondField).width(50).padLeft(5);

        add(controlsTable).fillX().expandX();

        // Setup listeners
        setupListeners();

        // Initially hide the menu
        setVisible(false);

        // Set initial state based on the game's real-time status
        updateButtonState();
    }

    /**
     * Sets up listeners for UI components.
     */
    private void setupListeners() {
        // Start/Pause button listener
        startPauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (screen.isRealTimeEnabled() && !screen.isRealTimePaused()) {
                    // If running, pause it
                    screen.pauseRealTimeSimulation();
                } else if (screen.isRealTimeEnabled() && screen.isRealTimePaused()) {
                    // If paused, resume it
                    screen.resumeRealTimeSimulation();
                } else {
                    // Not running, start it
                    try {
                        // Parse steps per second
                        float stepsPerSecond = Float.parseFloat(stepsPerSecondField.getText());
                        // Set speed and start simulation
                        screen.setRealTimeSpeed(stepsPerSecond);
                        screen.startRealTimeSimulation();
                    } catch (NumberFormatException e) {
                        // If parsing fails, use default value
                        screen.setRealTimeSpeed(5);
                        screen.startRealTimeSimulation();
                        stepsPerSecondField.setText("5");
                    }
                }

                // Update button text
                updateButtonState();
            }
        });

        // Steps per second field listener
        stepsPerSecondField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float stepsPerSecond = Float.parseFloat(stepsPerSecondField.getText());
                    screen.setRealTimeSpeed(stepsPerSecond);
                } catch (NumberFormatException e) {
                    // Invalid input, don't change the speed
                }

                // Prevent the event from propagating further
                event.stop();
            }
        });
    }

    /**
     * Updates the button text based on simulation state.
     */
    public void updateButtonState() {
        if (screen.isRealTimeEnabled()) {
            if (screen.isRealTimePaused()) {
                startPauseButton.setText("Resume");
            } else {
                startPauseButton.setText("Pause");
            }
        } else {
            startPauseButton.setText("Start");
        }
    }

    /**
     * Shows the menu aligned with the specified actor.
     *
     * @param stage the stage
     * @param actor the actor to align with
     */
    public void showMenu(Stage stage, Actor actor) {
        // First pack the menu to calculate its dimensions
        pack();

        // Calculate coordinates: center the menu horizontally with the button
        // and position it just below the button
        float buttonX = actor.getX();
        float buttonY = actor.getY();
        float buttonWidth = actor.getWidth();

        // Get the menu width and height
        float menuWidth = getWidth();
        float menuHeight = getHeight();

        // Calculate the position to center the menu horizontally relative to the button
        float x = buttonX + (buttonWidth / 2) - (menuWidth / 2);

        // Position the menu just below the button
        float y = buttonY - menuHeight - 1;

        // Set the position and add to stage if not already
        setPosition(x, y);

        if (getStage() == null) {
            stage.addActor(this);
        }

        setVisible(true);
    }

    /**
     * Hides the menu.
     */
    public void hideMenu() {
        setVisible(false);
    }
}
