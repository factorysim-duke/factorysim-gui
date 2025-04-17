package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;

public class TopBar extends VisTable {
    private VisLabel stepCountLabel;
    private VisTextButton saveButton;
    private VisTextButton loadButton;
    private VisTextButton realTimeButton;

    public TopBar(int initialStep) {
        super();
        init(initialStep);
    }

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
        
        // real-time button
        realTimeButton = new VisTextButton("Real-time", "blue");
        realTimeButton.pad(5, 10, 5, 10);

        // combine buttons
        VisTable rightButtons = new VisTable();
        rightButtons.add(realTimeButton).padRight(20);
        rightButtons.add(loadButton).padRight(20);
        rightButtons.add(saveButton);


        // add to table
        add(titleLabel).left().padLeft(20);
        add(stepCountLabel).center().expandX();
        add(rightButtons).right();
    }

    public void updateStepCount(int currentStep) {
        stepCountLabel.setText("Current Step: " + currentStep);
    }

    public VisTextButton getSaveButton() {
        return saveButton;
    }

    public VisTextButton getLoadButton() {
        return loadButton;
    }
    
    public VisTextButton getRealTimeButton() {
        return realTimeButton;
    }
}
