package edu.duke.ece651.factorysim.screen.util;

import com.badlogic.gdx.Gdx;

import edu.duke.ece651.factorysim.Logger;
import edu.duke.ece651.factorysim.screen.ui.LogPanel;

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
