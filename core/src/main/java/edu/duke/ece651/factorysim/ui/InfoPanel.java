package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.Building;

public class InfoPanel extends VisTable {
    private VisLabel buildingLabel;
    private VisSelectBox<String> policyBox;
    private VisLabel outputsLabel;
    private VisLabel sourcesLabel;
    private VisLabel queueLabel;
    private VisTextButton newRequestButton;

    public InfoPanel() {
        super();
        init();
    }

    private void init() {
        setVisible(false);
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        top();

        buildingLabel = new VisLabel("Building Info");
        buildingLabel.setColor(Color.BLACK);

        VisLabel policyLabel = new VisLabel("Policy:");
        policyLabel.setColor(Color.BLACK);

        policyBox = new VisSelectBox<>("blue");
        policyBox.setItems("FIFO");

        outputsLabel = new VisLabel("Outputs:");
        outputsLabel.setColor(Color.BLACK);

        sourcesLabel = new VisLabel("Sources:");
        sourcesLabel.setColor(Color.BLACK);

        queueLabel = new VisLabel("Request Queue:");
        queueLabel.setColor(Color.BLACK);

        newRequestButton = new VisTextButton("New Request", "blue");

        // update layout
        add(buildingLabel).left().padLeft(10).padTop(10).row();
        add(policyLabel).left().padLeft(10).padTop(5);
        add(policyBox).left().padLeft(5).pad(5, 10, 5, 10).row();
        add(outputsLabel).left().padLeft(10).padTop(5).row();
        add(sourcesLabel).left().padLeft(10).padTop(5).row();
        add(queueLabel).left().padLeft(10).padTop(5).row();
        add(newRequestButton).fillX().pad(10).row();
    }

    public void showBuildingInfo(Building building) {
        setVisible(true);
        buildingLabel.setText("Building '" + building.getName() + "'");
        outputsLabel.setText("Outputs: door");
        sourcesLabel.setText("Sources:");
        queueLabel.setText("Request Queue:");
    }

    public VisSelectBox<String> getPolicyBox() {
        return policyBox;
    }

    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }
}
