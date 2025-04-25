package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import edu.duke.ece651.factorysim.Constants;

/**
 * Top bar of the simulation.
 */
public class TopBar extends VisTable {
    private VisLabel stepCountLabel;
    private VisTextButton saveButton;
    private VisTextButton loadButton;
    private VisTextButton backToHomeButton;
    /**
     * Constructor for the TopBar class.
     * @param initialStep the initial step
     */
    public TopBar(int initialStep, Stage stage) {
        super();
        init(initialStep, stage);
    }

    /**
     * Initialize the top bar.
     * @param currentStep the current step
     */
    private void init(int currentStep, Stage stage) {
        // title
        VisLabel titleLabel = new VisLabel("Factorysim");
        titleLabel.setFontScale(2.2f);

        // top bar height
        float topBarHeight = titleLabel.getPrefHeight();

        // step count
        stepCountLabel = new VisLabel("Current Step: " + currentStep);

        // back to home button
        backToHomeButton = new VisTextButton("Back to Home", "blue");
        backToHomeButton.pad(5, 10, 5, 10);

        // save button
        saveButton = new VisTextButton("Save", "orange");
        saveButton.pad(5, 10, 5, 10);

        // load button
        loadButton = new VisTextButton("Load", "orange");
        loadButton.pad(5, 10, 5, 10);

        // create left side with title
        VisTable leftSide = new VisTable();
        leftSide.add(titleLabel).padLeft(20).left();

        // create right side with buttons
        VisTable rightSide = new VisTable();
        rightSide.add(backToHomeButton).padRight(20);
        rightSide.add(loadButton).padRight(20);
        rightSide.add(saveButton).padRight(5);

        // add left side, step count, and right side to the table
        add(leftSide).expandX().left();
        stage.addActor(stepCountLabel);
        stepCountLabel.setPosition(
          (Constants.WINDOW_WIDTH - stepCountLabel.getPrefWidth()) / 2,
          Constants.WINDOW_HEIGHT - topBarHeight/2 - stepCountLabel.getPrefHeight()/2
        );
        add(rightSide).expandX().right();
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

    /**
     * Get the back to home button.
     * @return the back to home button
     */
    public VisTextButton getBackToHomeButton() {
        return backToHomeButton;
    }
}
