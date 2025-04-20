package edu.duke.ece651.factorysim.screen.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.screen.SimulationScreen;

/**
 * Listeners for the request and source policies of a building.
 */
public class PolicyChangeListener {
    private final SimulationScreen screen;
    private final Building building;

    /**
     * Constructor for the PolicyChangeListener class.
     * @param screen is the SimulationScreen instance
     * @param building is the building to listen to
     */
    public PolicyChangeListener(SimulationScreen screen, Building building) {
        this.screen = screen;
        this.building = building;
    }

    /**
     * Create a listener for the request policy of a building.
     * @param policyBox is the VisSelectBox instance
     * @return a ChangeListener instance
     */
    public ChangeListener createRequestPolicyListener(VisSelectBox<String> policyBox) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedPolicy = policyBox.getSelected().toLowerCase();
                String buildingName = building.getName();
                screen.setPolicy("request", selectedPolicy, buildingName);
                System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
            }
        };
    }

    /**
     * Create a listener for the source policy of a building.
     * @param policyBox is the VisSelectBox instance
     * @return a ChangeListener instance
     */
    public ChangeListener createSourcePolicyListener(VisSelectBox<String> policyBox) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedPolicy = policyBox.getSelected().toLowerCase();
                String buildingName = building.getName();
                screen.setPolicy("source", selectedPolicy, buildingName);
                System.out.println("Setting source policy to " + selectedPolicy + " for building " + buildingName);
            }
        };
    }
}
