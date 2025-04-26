package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.StorageBuilding;
import edu.duke.ece651.factorysim.Building;

/**
 * Storage info panel.
 */
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

    // Width for title label
    private static final float TITLE_WIDTH = 200f;

    /**
     * Constructor for the StorageInfoPanel class.
     * @param building the storage building to display information for
     */
    public StorageInfoPanel(StorageBuilding building) {
        super();
        this.building = building;

        // Storage title with wrapping
        buildingLabel = createWrappedTitleLabel("Storage: " + building.getName());
        add(buildingLabel).left().width(TITLE_WIDTH).padBottom(10).row();

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

    /**
     * Update the data for the storage info panel.
     * @param building the storage building to display information for
     */
    public void updateData(StorageBuilding building) {
        capacityLabel.setText("Capacity: " + building.getMaxCapacity());
        currentLoadLabel.setText("Current Load: " + building.getCurrentStockNum());
    }

    /**
     * Update the data for the storage info panel.
     * Override the parent class method to handle different data types.
     *
     * @param data the object containing building data
     */
    @Override
    public void updateData(Object data) {
        updateData((StorageBuilding) data);
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
