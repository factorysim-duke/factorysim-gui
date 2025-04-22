package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import edu.duke.ece651.factorysim.screen.HomeScreen;

/**
 * Represents a libGDX game application of factorysim.
 */
public class FactoryGame extends Game {
    @Override
    public void create() {
        // Create home screen and set it as the initial screen
        HomeScreen homeScreen = new HomeScreen(this);
        this.setScreen(homeScreen);
    }
}
