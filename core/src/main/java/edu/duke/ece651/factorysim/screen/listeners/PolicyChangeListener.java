package edu.duke.ece651.factorysim.screen.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.FactoryGame;

public class PolicyChangeListener {
    private final FactoryGame game;
    private final Building building;

    public PolicyChangeListener(FactoryGame game, Building building) {
        this.game = game;
        this.building = building;
    }

    public ChangeListener createRequestPolicyListener(VisSelectBox<String> policyBox) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedPolicy = policyBox.getSelected().toLowerCase();
                String buildingName = building.getName();
                game.setPolicy("request", selectedPolicy, buildingName);
                System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
            }
        };
    }

    public ChangeListener createSourcePolicyListener(VisSelectBox<String> policyBox) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedPolicy = policyBox.getSelected().toLowerCase();
                String buildingName = building.getName();
                game.setPolicy("source", selectedPolicy, buildingName);
                System.out.println("Setting source policy to " + selectedPolicy + " for building " + buildingName);
            }
        };
    }
}
