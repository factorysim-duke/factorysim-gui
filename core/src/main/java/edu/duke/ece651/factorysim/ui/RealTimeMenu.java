package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.PopupMenu;
import edu.duke.ece651.factorysim.FactoryGame;

/**
 * A dropdown menu for real-time simulation controls.
 */
public class RealTimeMenu extends PopupMenu {
    private VisTextButton startPauseButton;
    private VisTextField stepsPerSecondField;
    private final FactoryGame game;
    
    /**
     * Creates a real-time controls menu.
     * 
     * @param game the game instance
     */
    public RealTimeMenu(final FactoryGame game) {
        super();
        this.game = game;
        
        // Create the menu layout
        VisTable menuTable = new VisTable();
        menuTable.pad(5);
        
        // Start/Pause button
        startPauseButton = new VisTextButton("Start", "orange");
        
        // Steps per second label and field
        VisLabel stepsPerSecondLabel = new VisLabel("Steps per second: ");
        stepsPerSecondField = new VisTextField("10");
        stepsPerSecondField.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
        
        // Add components to the menu
        menuTable.add(startPauseButton).fillX().expandX().padBottom(5).row();
        
        VisTable controlsTable = new VisTable();
        controlsTable.add(stepsPerSecondLabel).left();
        controlsTable.add(stepsPerSecondField).width(50).padLeft(5);
        
        menuTable.add(controlsTable).fillX().expandX();
        
        // Add the table to the popup menu
        add(menuTable);
        
        // Setup listeners
        setupListeners();
        
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
                if (game.isRealTimeEnabled() && !game.isRealTimePaused()) {
                    // If running, pause it
                    game.pauseRealTimeSimulation();
                } else if (game.isRealTimeEnabled() && game.isRealTimePaused()) {
                    // If paused, resume it
                    game.resumeRealTimeSimulation();
                } else {
                    // Not running, start it
                    try {
                        // Parse steps per second
                        float stepsPerSecond = Float.parseFloat(stepsPerSecondField.getText());
                        // Set speed and start simulation
                        game.setRealTimeSpeed(stepsPerSecond);
                        game.startRealTimeSimulation();
                    } catch (NumberFormatException e) {
                        // If parsing fails, use default value
                        game.setRealTimeSpeed(10);
                        game.startRealTimeSimulation();
                        stepsPerSecondField.setText("10");
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
                    game.setRealTimeSpeed(stepsPerSecond);
                } catch (NumberFormatException e) {
                    // Invalid input, don't change the speed
                }
            }
        });
    }
    
    /**
     * Updates the button text based on simulation state.
     */
    public void updateButtonState() {
        if (game.isRealTimeEnabled()) {
            if (game.isRealTimePaused()) {
                startPauseButton.setText("Resume");
            } else {
                startPauseButton.setText("Pause");
            }
        } else {
            startPauseButton.setText("Start");
        }
    }
} 