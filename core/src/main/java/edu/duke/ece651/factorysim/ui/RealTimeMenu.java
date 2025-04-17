package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        menuTable.setBackground(new VisTable().getBackground());
        menuTable.setColor(new Color(0.2f, 0.4f, 0.8f, 1f)); // Blue background
        menuTable.pad(10);
        
        // Title label
        VisLabel titleLabel = new VisLabel("Real-time");
        titleLabel.setColor(Color.WHITE);
        
        // Start/Pause button
        startPauseButton = new VisTextButton("Start", "orange");
        
        // Steps per second label and field
        VisLabel stepsPerSecondLabel = new VisLabel("Steps per second: ");
        stepsPerSecondLabel.setColor(Color.WHITE);
        stepsPerSecondField = new VisTextField("10");
        stepsPerSecondField.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
        
        // Prevent menu from closing when clicking on the text field
        stepsPerSecondField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop(); // Stop event propagation to prevent menu from closing
            }
        });
        
        // Add components to the menu
        menuTable.add(titleLabel).center().padBottom(10).row();
        menuTable.add(startPauseButton).fillX().expandX().padBottom(10).row();
        
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
                
                // Prevent the event from propagating further
                event.stop();
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
    
    /**
     * Shows the menu aligned with the specified actor.
     * 
     * @param stage the stage
     * @param actor the actor to align with
     */
    @Override
    public void showMenu(Stage stage, Actor actor) {
        // First pack the menu to calculate its dimensions
        pack();
        
        // Calculate coordinates: center the menu horizontally with the button
        // and position it just below the button
        float buttonX = actor.getX();
        float buttonY = actor.getY();
        float buttonWidth = actor.getWidth();
        float buttonHeight = actor.getHeight();
        
        // Get the menu width and height
        float menuWidth = getWidth();
        float menuHeight = getHeight();
        
        // Calculate the position to center the menu horizontally relative to the button
        float x = buttonX + (buttonWidth / 2) - (menuWidth / 2);
        
        // Position the menu just below the button
        float y = buttonY - menuHeight;
        
        // Make sure the menu stays within the stage bounds
        // Get stage dimensions
        float stageWidth = stage.getWidth();
        float stageHeight = stage.getHeight();
        
        // Adjust x position if menu would go off the right edge
        if (x + menuWidth > stageWidth) {
            x = stageWidth - menuWidth;
        }
        
        // Adjust x position if menu would go off the left edge
        if (x < 0) {
            x = 0;
        }
        
        // Adjust y position if menu would go off the bottom edge
        if (y < 0) {
            y = 0;
        }
        
        // Set the position and add to stage
        setPosition(x, y);
        stage.addActor(this);
    }
} 