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
    private Building building;

    public InfoPanel() {
        super();
        init();
    }

    public void setBuilding(Building building) {
        this.building = building;
        this.setVisible(true);
    }

    private void init() {
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        top();

        // Building label
        buildingLabel = new VisLabel("Building 'D'");
        buildingLabel.setColor(Color.BLACK);

        // Policy controls
        VisLabel policyLabel = new VisLabel("Policy:");
        policyLabel.setColor(Color.BLACK);
        policyBox = new VisSelectBox<>("blue");
        policyBox.setItems("FIFO");

        // Outputs label
        outputsLabel = new VisLabel("Outputs: door");
        outputsLabel.setColor(Color.BLACK);

        // Sources label
        sourcesLabel = new VisLabel("Sources:");
        sourcesLabel.setColor(Color.BLACK);

        // Queue label
        queueLabel = new VisLabel("Request Queue:");
        queueLabel.setColor(Color.BLACK);

        // New request button
        newRequestButton = new VisTextButton("New Request", "blue");

        // Add components to panel
        add(buildingLabel).left().padLeft(10).padTop(10).row();
        add(policyLabel).left().padLeft(10).padTop(5);
        add(policyBox).left().padLeft(5).pad(5, 10, 5, 10).row();
        add(outputsLabel).left().padLeft(10).padTop(5).row();
        add(sourcesLabel).left().padLeft(10).padTop(5).row();
        add(queueLabel).left().padLeft(10).padTop(5).row();
        add(newRequestButton).fillX().pad(10).row();
    }

    public void setBuildingName(String name) {
        buildingLabel.setText("Building '" + name + "'");
    }

    public void setOutputs(String outputs) {
        outputsLabel.setText("Outputs: " + outputs);
    }

    public void setSources(String sources) {
        sourcesLabel.setText("Sources: " + sources);
    }

    public void setQueueInfo(String queueInfo) {
        queueLabel.setText("Request Queue: " + queueInfo);
    }

    public VisSelectBox<String> getPolicyBox() {
        return policyBox;
    }

    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }
}
