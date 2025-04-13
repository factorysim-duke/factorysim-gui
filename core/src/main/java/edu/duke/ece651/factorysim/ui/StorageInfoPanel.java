package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.StorageBuilding;

public class StorageInfoPanel extends InfoPanel {
    private final VisLabel buildingLabel;
    private final VisLabel storesLabel;
    private final VisLabel capacityLabel;
    private final VisLabel priorityLabel;
    private final VisLabel currentStockLabel;

    public StorageInfoPanel(StorageBuilding building) {
        super();

        buildingLabel = new VisLabel("Storage: " + building.getName());
        buildingLabel.setColor(Color.BLACK);

        storesLabel = new VisLabel();
        storesLabel.setColor(Color.BLACK);

        capacityLabel = new VisLabel();
        capacityLabel.setColor(Color.BLACK);

        priorityLabel = new VisLabel();
        priorityLabel.setColor(Color.BLACK);

        currentStockLabel = new VisLabel();
        currentStockLabel.setColor(Color.BLACK);

        add(buildingLabel).left().padLeft(10).padTop(10).row();
        add(storesLabel).left().padLeft(10).padTop(5).row();
        add(capacityLabel).left().padLeft(10).padTop(5).row();
        add(priorityLabel).left().padLeft(10).padTop(5).row();
        add(currentStockLabel).left().padLeft(10).padTop(5).row();

        updateData(building);
    }

    public void updateData(StorageBuilding building) {
        storesLabel.setText("Stores: " + building.getStorageItem().getName());
        capacityLabel.setText("Capacity: " + building.getMaxCapacity());
        priorityLabel.setText("Priority: " + building.getPriority());
        currentStockLabel.setText("Current Stock: " + building.getCurrentStockNum());
    }
}
