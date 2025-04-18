package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.FactoryBuilding;
import edu.duke.ece651.factorysim.Recipe;
import edu.duke.ece651.factorysim.Building;

import java.util.List;
import java.util.stream.Collectors;

public class FactoryInfoPanel extends InfoPanel {
    private final FactoryBuilding building;
    private final VisLabel buildingLabel;
    private final VisLabel outputsLabel;
    private final VisLabel sourcesLabel;
    private final VisLabel queueLabel;

    private final VisLabel requestPolicyLabel;
    private final VisSelectBox<String> requestPolicyBox;
    private final VisTextButton newRequestButton;

    private final VisLabel sourcePolicyLabel;
    private final VisSelectBox<String> sourcePolicyBox;

    /*
     * FactoryInfoPanel constructor
     *
     * @param building the factory building to display information for
     */
    public FactoryInfoPanel(FactoryBuilding building) {
        super();
        this.building = building;

        // Factory title
        buildingLabel = new VisLabel("Factory: " + building.getName());
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

        // Outputs label
        outputsLabel = new VisLabel();
        outputsLabel.setColor(Color.DARK_GRAY);
        add(outputsLabel).left().padBottom(5).row();

        // Sources label
        sourcesLabel = new VisLabel();
        sourcesLabel.setColor(Color.DARK_GRAY);
        add(sourcesLabel).left().padBottom(5).row();

        // Queue label
        queueLabel = new VisLabel();
        queueLabel.setColor(Color.DARK_GRAY);
        add(queueLabel).left().padBottom(15).row();

        // New request button
        newRequestButton = new VisTextButton("New Request", "blue");
        add(newRequestButton).fillX().height(32);

        updateData(building);
    }

    public void updateData(FactoryBuilding building) {
        List<Recipe> factoryRecipes = building.getFactoryType().getRecipes();
        String recipeNames = factoryRecipes.stream()
                .map(r -> r.getOutput().getName())
                .collect(Collectors.joining(", "));
        outputsLabel.setText("Outputs: " + recipeNames);

        String sourceNames = building.getSources().stream()
                .map(b -> b.getName())
                .collect(Collectors.joining(", "));
        sourcesLabel.setText("Sources: " + sourceNames);

        int queueSize = building.getPendingRequests().size();
        queueLabel.setText("Request Queue: " + queueSize + " pending");
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
