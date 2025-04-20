package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.MineBuilding;
import edu.duke.ece651.factorysim.Building;

/**
 * Mine info panel.
 */
public class MineInfoPanel extends InfoPanel {
    private final MineBuilding building;
    private final VisLabel buildingLabel;
    private final VisLabel resourceLabel;
    private final VisLabel latencyLabel;
    private final VisLabel requestPolicyLabel;
    private final VisSelectBox<String> requestPolicyBox;
    private final VisLabel sourcePolicyLabel;
    private final VisSelectBox<String> sourcePolicyBox;
    private final VisTextButton newRequestButton;

    /**
     * Constructor for the MineInfoPanel class.
     * @param building the mine building to display information for
     */
    public MineInfoPanel(MineBuilding building) {
        super();
        this.building = building;

        buildingLabel = new VisLabel("Mine: " + building.getName());
        buildingLabel.setColor(Color.BLACK);
        buildingLabel.setFontScale(1.2f);
        add(buildingLabel).left().padBottom(10).row();

        // Request policy
        requestPolicyLabel = new VisLabel("Request Policy:");
        requestPolicyLabel.setColor(Color.BLACK);
        requestPolicyBox = new VisSelectBox<>("blue");
        requestPolicyBox.setItems("FIFO", "READY", "SJF");

        VisTable requestPolicyRow = new VisTable(true);
        requestPolicyRow.add(requestPolicyLabel).left();
        requestPolicyRow.add(requestPolicyBox).width(100).left();
        add(requestPolicyRow).left().padBottom(10).row();

        // Source policy
        sourcePolicyLabel = new VisLabel("Source Policy:");
        sourcePolicyLabel.setColor(Color.BLACK);
        sourcePolicyBox = new VisSelectBox<>("blue");
        sourcePolicyBox.setItems("QLEN", "SIMPLELAT", "RECURSIVELAT");

        VisTable sourcePolicyRow = new VisTable(true);
        sourcePolicyRow.add(sourcePolicyLabel).left();
        sourcePolicyRow.add(sourcePolicyBox).width(100).left();
        add(sourcePolicyRow).left().padBottom(10).row();

        resourceLabel = new VisLabel();
        resourceLabel.setColor(Color.DARK_GRAY);

        latencyLabel = new VisLabel();
        latencyLabel.setColor(Color.DARK_GRAY);

        add(resourceLabel).left().padLeft(10).padTop(5).row();
        add(latencyLabel).left().padLeft(10).padTop(5).row();

        // New request button
        newRequestButton = new VisTextButton("New Request", "blue");
        add(newRequestButton).fillX().height(32).padTop(15);

        updateData(building);
    }

    /**
     * Update the data for the mine info panel.
     * @param building the mine building to display information for
     */
    public void updateData(MineBuilding building) {
        resourceLabel.setText("Resource: " + building.getResource().getName());
        latencyLabel.setText("Mining Latency: " + building.getMiningLatency());
    }

    /**
     * Get the request policy box.
     * @return the request policy box
     */
    public VisSelectBox<String> getRequestPolicyBox() {
        return requestPolicyBox;
    }

    /**
     * Get the source policy box.
     * @return the source policy box
     */
    public VisSelectBox<String> getSourcePolicyBox() {
        return sourcePolicyBox;
    }

    /**
     * Get the new request button.
     * @return the new request button
     */
    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }

    /**
     * Get the building.
     * @return the building
     */
    public Building getBuilding() {
        return building;
    }
}
