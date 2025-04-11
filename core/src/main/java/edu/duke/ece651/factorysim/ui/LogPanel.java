package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class LogPanel extends VisTable {
    private VisTextArea logArea;
    private VisSelectBox<String> verbosityBox;

    public LogPanel() {
        super();
        init();
    }

    private void init() {
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        top();

        // Logs label
        VisLabel logsLabel = new VisLabel("Logs");
        logsLabel.setColor(Color.BLACK);

        // Verbosity controls
        verbosityBox = new VisSelectBox<>("blue");
        verbosityBox.setItems("0", "1", "2");

        VisTable verboseTable = new VisTable();
        VisLabel verboseLabel = new VisLabel("verbose:");
        verboseLabel.setColor(Color.BLACK);
        verboseTable.add(verboseLabel).left();
        verboseTable.add(verbosityBox).left().padLeft(5).pad(5, 10, 5, 10);

        // Log area
        logArea = new VisTextArea("");
        logArea.setDisabled(true);

        // Add components to panel
        add(logsLabel).left().padLeft(10).padTop(10).row();
        add(verboseTable).left().padLeft(10).padTop(5).row();
        add(logArea).expand().fill().pad(10);
    }

    public void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    public void clearLogs() {
        logArea.setText("");
    }

    public VisSelectBox<String> getVerbosityBox() {
        return verbosityBox;
    }
}
