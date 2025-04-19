package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

/**
 * Log panel.
 */
public class LogPanel extends VisTable {
    private VisLabel logLabel;
    private VisScrollPane scrollPane;
    private VisSelectBox<String> verbosityBox;

    /**
     * Constructor for the LogPanel class.
     */
    public LogPanel() {
        super();
        init();
    }

    /**
     * Initialize the log panel.
     */
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

        add(logsLabel).left().padLeft(10).padTop(10).row();
        add(verboseTable).left().padLeft(10).padTop(5).row();

        // Log label setup
        logLabel = new VisLabel("");
        logLabel.setWrap(true);
        logLabel.setColor(Color.WHITE);
        logLabel.setTouchable(Touchable.disabled);
        logLabel.setAlignment(Align.topLeft);

        // Style background
        Drawable paddedBackground = VisUI.getSkin().newDrawable("white", Color.DARK_GRAY);
        paddedBackground.setLeftWidth(1f);
        paddedBackground.setRightWidth(1f);
        paddedBackground.setTopHeight(1f);
        paddedBackground.setBottomHeight(1f);

        // Create a table to hold the label with background
        VisTable logTable = new VisTable();
        logTable.setBackground(paddedBackground);
        logTable.top();
        logTable.add(logLabel).pad(8).expand().fill().top().left();


        // ScrollPane wrapping logTable
        scrollPane = new VisScrollPane(logTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setFlickScroll(true);
        scrollPane.setTouchable(Touchable.enabled);
        scrollPane.setOverscroll(false, false);

        add(scrollPane).expand().fill().pad(10).row();
    }

    /**
     * Append a log message to the log panel.
     * @param message the message to append
     */
    public void appendLog(String message) {
        logLabel.setText(logLabel.getText() + message + "\n");
        scrollPane.layout();
        scrollPane.setScrollPercentY(1f);
    }

    /**
     * Clear the logs.
     */
    public void clearLogs() {
        logLabel.setText("");
    }

    /**
     * Get the verbosity box.
     * @return the verbosity box
     */
    public VisSelectBox<String> getVerbosityBox() {
        return verbosityBox;
    }
}
