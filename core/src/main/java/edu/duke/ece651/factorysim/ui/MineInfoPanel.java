package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.MineBuilding;

public class MineInfoPanel extends InfoPanel {
    private final VisLabel buildingLabel;
    private final VisLabel resourceLabel;
    private final VisLabel latencyLabel;

    public MineInfoPanel(MineBuilding building) {
        super();

        buildingLabel = new VisLabel("Mine: " + building.getName());
        buildingLabel.setColor(Color.BLACK);

        resourceLabel = new VisLabel();
        resourceLabel.setColor(Color.BLACK);

        latencyLabel = new VisLabel();
        latencyLabel.setColor(Color.BLACK);

        add(buildingLabel).left().padLeft(10).padTop(10).row();
        add(resourceLabel).left().padLeft(10).padTop(5).row();
        add(latencyLabel).left().padLeft(10).padTop(5).row();

        updateData(building);
    }

    public void updateData(MineBuilding building) {
        resourceLabel.setText("Resource: " + building.getResource().getName());
        latencyLabel.setText("Mining Latency: " + building.getMiningLatency());
    }
}
