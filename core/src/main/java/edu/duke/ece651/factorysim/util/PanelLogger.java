package edu.duke.ece651.factorysim.util;

import edu.duke.ece651.factorysim.Logger;
import edu.duke.ece651.factorysim.ui.LogPanel;
import com.badlogic.gdx.Gdx;

public class PanelLogger implements Logger {
    private final LogPanel logPanel;

    public PanelLogger(LogPanel panel) {
        this.logPanel = panel;
    }

    @Override
    public void log(String message) {
        Gdx.app.postRunnable(() -> {
            logPanel.appendLog(message);
        });
    }
}
