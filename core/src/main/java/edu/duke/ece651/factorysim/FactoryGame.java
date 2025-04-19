package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.viewport.*;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
import edu.duke.ece651.factorysim.util.PanelLogger;

/**
 * Represents a libGDX game application of factorysim.
 */
public class FactoryGame extends Game {
    @Override
    public void create() {
        // Create screen and set screen as the screen of the game
        SimulationScreen simulationScreen = new SimulationScreen();
        this.setScreen(simulationScreen);
    }
}
