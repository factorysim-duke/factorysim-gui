package edu.duke.ece651.factorysim.screen.ui;

import edu.duke.ece651.factorysim.*;

/**
 * Factory for creating info panels for buildings.
 */
public class BuildingInfoPanelFactory {
    /**
     * Create an info panel for a building.
     * @param b is the building to create an info panel for
     * @return an InfoPanel instance
     */
    public static InfoPanel createInfoPanel(Building b) {
        if (b instanceof FactoryBuilding factory) {
            return new FactoryInfoPanel(factory);
        } else if (b instanceof MineBuilding mine) {
            return new MineInfoPanel(mine);
        } else if (b instanceof StorageBuilding storage) {
            return new StorageInfoPanel(storage);
        } else if (b instanceof DronePortBuilding port) {
            return new DronePortInfoPanel(port);
        } else if (b instanceof WasteDisposalBuilding disposal) {
            return new WasteDisposalInfoPanel(disposal);
        }
        throw new IllegalArgumentException("Unknown building type: " + b.getClass().getSimpleName());
    }
}

