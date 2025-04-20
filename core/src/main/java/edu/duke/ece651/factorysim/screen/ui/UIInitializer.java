package edu.duke.ece651.factorysim.screen.ui;

import com.kotcrab.vis.ui.VisUI;
import edu.duke.ece651.factorysim.screen.ui.style.UIButtonStyle;
import edu.duke.ece651.factorysim.screen.ui.style.UISelectBoxStyle;

/**
 * Initializes the UI.
 */
public class UIInitializer {

    /**
     * Initialize the VisUI.
     */
    public void initializeVisUI() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
    }

    /**
     * Register custom styles.
     */
    public void registerCustomStyles() {
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();
    }
}
