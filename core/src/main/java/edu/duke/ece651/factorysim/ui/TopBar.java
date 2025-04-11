package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;

public class TopBar extends VisTable {
    private VisLabel stepCountLabel;
    private VisTextButton saveButton;
    private VisTextButton loadButton;

    public TopBar() {
        super();
        init();
    }

    private void init() {
        // title
        VisLabel titleLabel = new VisLabel("Factorysim");
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(Color.BLACK);

        // step count
        stepCountLabel = new VisLabel("Current Step: 0");
        stepCountLabel.setColor(Color.BLACK);

        // save button
        saveButton = new VisTextButton("Save", "orange");
        saveButton.pad(5, 10, 5, 10);

        // load button
        loadButton = new VisTextButton("Load", "orange");
        loadButton.pad(5, 10, 5, 10);

        // combine save and load buttons
        VisTable rightButtons = new VisTable();
        rightButtons.add(saveButton).padRight(20);
        rightButtons.add(loadButton);

        // add to table
        add(titleLabel).left().padLeft(20);
        add(stepCountLabel).center().expandX();
        add(rightButtons).right();
    }

    public void updateStepCount(int step) {
        stepCountLabel.setText("Current Step: " + step);
    }

    public VisTextButton getSaveButton() {
        return saveButton;
    }

    public VisTextButton getLoadButton() {
        return loadButton;
    }
}
