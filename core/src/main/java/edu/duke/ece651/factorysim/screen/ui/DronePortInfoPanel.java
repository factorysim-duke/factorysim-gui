package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.DronePortBuilding;
import edu.duke.ece651.factorysim.Building;

/**
 * Info panel for drone ports.
 */
public class DronePortInfoPanel extends InfoPanel {
    private final DronePortBuilding port;

    private final VisLabel titleLabel;
    private final VisLabel droneCountLabel;
    private final VisLabel maxDronesLabel;
    private final VisTextButton addDroneButton;

    // Width for title label
    private static final float TITLE_WIDTH = 200f;

    public DronePortInfoPanel(DronePortBuilding port) {
        super();
        this.port = port;

        // Drone port title with wrapping
        titleLabel = createWrappedTitleLabel("Drone Port: " + port.getName());
        add(titleLabel).left().width(TITLE_WIDTH).padBottom(10).row();

        droneCountLabel = new VisLabel();
        droneCountLabel.setColor(Color.DARK_GRAY);
        add(droneCountLabel).left().padTop(5).row();

        maxDronesLabel = new VisLabel();
        maxDronesLabel.setColor(Color.DARK_GRAY);
        add(maxDronesLabel).left().padTop(5).row();

        addDroneButton = new VisTextButton("Add Drone", "blue");
        add(addDroneButton).fillX().height(32).padTop(15).row();

        addDroneButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean success = port.createDrone();
                if (!success) {
                    System.out.println("Max drones reached.");
                }
                updateData();
            }
        });

        updateData();
    }

    /**
     * Refreshes the drone count info.
     */
    public void updateData() {
        droneCountLabel.setText("Current Drones: " + port.getDroneCount());
        maxDronesLabel.setText("Max Drones: " + port.getMaxDrones());
    }

    /**
     * Update the data for the drone port info panel.
     * Override the parent class method to handle different data types.
     *
     * @param data the object containing building data
     */
    @Override
    public void updateData(Object data) {
        updateData();
    }

    public VisTextButton getAddDroneButton() {
        return addDroneButton;
    }

    @Override
    public DronePortBuilding getBuilding() {
        return port;
    }
}
