package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.StorageBuilding;
import edu.duke.ece651.factorysim.Building;

public class StorageInfoPanel extends InfoPanel {
    private final StorageBuilding building;
    private final VisLabel buildingLabel;
    private final VisLabel capacityLabel;
    private final VisLabel currentLoadLabel;

    private final VisLabel requestPolicyLabel;
    private final VisSelectBox<String> requestPolicyBox;

    private final VisLabel sourcePolicyLabel;
    private final VisSelectBox<String> sourcePolicyBox;

    private final VisTextButton newRequestButton;

    public StorageInfoPanel(StorageBuilding building) {
        super();
        this.building = building;

        buildingLabel = new VisLabel("Storage: " + building.getName());
        buildingLabel.setColor(Color.BLACK);
        buildingLabel.setFontScale(1.2f);
        add(buildingLabel).left().padBottom(10).row();

        // Request policy
        requestPolicyLabel = new VisLabel("Request Policy:");
        requestPolicyLabel.setColor(Color.BLACK);
        requestPolicyBox = new VisSelectBox<>("blue");
        requestPolicyBox.setItems("FIFO");

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

        capacityLabel = new VisLabel();
        capacityLabel.setColor(Color.DARK_GRAY);

        currentLoadLabel = new VisLabel();
        currentLoadLabel.setColor(Color.DARK_GRAY);

        add(capacityLabel).left().padLeft(10).padTop(5).row();
        add(currentLoadLabel).left().padLeft(10).padTop(5).row();

        // New request button
        newRequestButton = new VisTextButton("New Request", "blue");
        // add(newRequestButton).fillX().height(32).padTop(15);

        updateData(building);
    }

    public void updateData(StorageBuilding building) {
        capacityLabel.setText("Capacity: " + building.getMaxCapacity());
        currentLoadLabel.setText("Current Load: " + building.getCurrentStockNum());
    }

    public VisSelectBox<String> getRequestPolicyBox() {
        return requestPolicyBox;
    }

    public VisSelectBox<String> getSourcePolicyBox() {
        return sourcePolicyBox;
    }

    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }

    public Building getBuilding() {
        return building;
    }
}
