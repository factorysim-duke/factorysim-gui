package edu.duke.ece651.factorysim.screen.ui;

import com.kotcrab.vis.ui.VisUI;
import edu.duke.ece651.factorysim.screen.ui.style.UIButtonStyle;
import edu.duke.ece651.factorysim.screen.ui.style.UISelectBoxStyle;

public class UIInitializer {

    public void initializeVisUI() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
    }

    public void registerCustomStyles() {
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();
    }
}
