package edu.duke.ece651.factorysim.screen.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.screen.ui.ControlPanel;
import edu.duke.ece651.factorysim.screen.ui.TopBar;
import edu.duke.ece651.factorysim.screen.SimulationScreen;

/**
 * Event listeners for the UI components.
 */
public class UIEventListenerFactory {
    private final FactoryGame game;

    /**
     * Constructor for the UIEventListenerFactory class.
     * @param game is the FactoryGame instance
     */
    public UIEventListenerFactory(FactoryGame game) {
        this.game = game;
    }

    /**
     * Create a listener for the load button.
     * @param stage is the Stage instance
     * @param fileChooser is the FileChooser instance
     * @param topBar is the TopBar instance
     * @return a ChangeListener instance
     */
    public ChangeListener createLoadButtonListener(Stage stage, FileChooser fileChooser, TopBar topBar) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(fileChooser.fadeIn());
                topBar.updateStepCount(game.getCurrentStep());
            }
        };
    }

    /**
     * Create a listener for the save button.
     * @param stage is the Stage instance
     * @param fileChooser is the FileChooser instance
     * @param topBar is the TopBar instance
     * @return a ChangeListener instance
     */
    public ChangeListener createSaveButtonListener(Stage stage, FileChooser fileChooser, TopBar topBar) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(fileChooser.fadeIn());
                topBar.updateStepCount(game.getCurrentStep());
            }
        };
    }

    /**
     * Create a listener for the verbosity change.
     * @param game is the FactoryGame instance
     * @return a ChangeListener instance
     */
    public ChangeListener createVerbosityChangeListener(FactoryGame game) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (actor instanceof VisSelectBox<?>) {
                    @SuppressWarnings("unchecked")
                    VisSelectBox<String> box = (VisSelectBox<String>) actor;
                    game.setVerbosity(Integer.parseInt(box.getSelected()));
                }
            }
        };
    }

    /**
     * Create a listener for the step button.
     * @param game is the FactoryGame instance
     * @param controlPanel is the ControlPanel instance
     * @param screen is the SimulationScreen instance
     * @return a ClickListener instance
     */
    public ClickListener createStepButtonListener(FactoryGame game, ControlPanel controlPanel, SimulationScreen screen) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int stepsToMove = controlPanel.getStepCount();
                game.step(stepsToMove);
                screen.updateStepCount();
                screen.hideInfoPanel();
            }
        };
    }

    /**
     * Create a listener for the finish button.
     * @param game is the FactoryGame instance
     * @param topBar is the TopBar instance
     * @return a ClickListener instance
     */
    public ClickListener createFinishButtonListener(FactoryGame game, TopBar topBar) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.finish();
                topBar.updateStepCount(game.getCurrentStep());
            }
        };
    }
}