package edu.duke.ece651.factorysim.screen.ui;

import com.kotcrab.vis.ui.widget.*;

/**
 * Top bar of the simulation.
 */
public class TopBar extends VisTable {
    private VisLabel stepCountLabel;
    private VisTextButton saveButton;
    private VisTextButton loadButton;

    /**
     * Constructor for the TopBar class.
     * @param initialStep the initial step
     */
    public TopBar(int initialStep) {
        super();
        init(initialStep);
    }

    /**
     * Initialize the top bar.
     * @param currentStep the current step
     */
    private void init(int currentStep) {
        // title
        VisLabel titleLabel = new VisLabel("Factorysim");
        titleLabel.setFontScale(2.2f);

        // step count
        stepCountLabel = new VisLabel("Current Step: " + currentStep);

        // save button
        saveButton = new VisTextButton("Save", "orange");
        saveButton.pad(5, 10, 5, 10);

        // load button
        loadButton = new VisTextButton("Load", "orange");
        loadButton.pad(5, 10, 5, 10);

        // combine buttons
        VisTable rightButtons = new VisTable();
        rightButtons.add(loadButton).padRight(20);
        rightButtons.add(saveButton);

        // add to table
        add(titleLabel).left().padLeft(20);
        add(stepCountLabel).center().expandX();
        add(rightButtons).right();
    }

    /**
     * Update the step count.
     * @param currentStep the current step
     */
    public void updateStepCount(int currentStep) {
        stepCountLabel.setText("Current Step: " + currentStep);
    }

    /**
     * Get the save button.
     * @return the save button
     */
    public VisTextButton getSaveButton() {
        return saveButton;
    }

    /**
     * Get the load button.
     * @return the load button
     */
    public VisTextButton getLoadButton() {
        return loadButton;
    }
}
