package edu.duke.ece651.factorysim.screen.ui;

import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.FactoryBuilding;
import edu.duke.ece651.factorysim.MineBuilding;
import edu.duke.ece651.factorysim.StorageBuilding;

public class BuildingInfoPanelFactory {
    public static InfoPanel createInfoPanel(Building b) {
        if (b instanceof FactoryBuilding) {
            return new FactoryInfoPanel((FactoryBuilding) b);
        } else if (b instanceof MineBuilding) {
            return new MineInfoPanel((MineBuilding) b);
        } else if (b instanceof StorageBuilding) {
            return new StorageInfoPanel((StorageBuilding) b);
        } else {
            throw new IllegalArgumentException("Unknown building type: " + b.getClass().getSimpleName());
        }
    }
}

