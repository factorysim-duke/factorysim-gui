package edu.duke.ece651.factorysim.screen.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.FactoryBuilding;
import edu.duke.ece651.factorysim.Recipe;
import edu.duke.ece651.factorysim.Building;

/**
 * Factory info panel.
 */
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

    // Width for title label
    private static final float TITLE_WIDTH = 200f;

    /**
     * Constructor for the FactoryInfoPanel class.
     *
     * @param building the factory building to display information for
     */
    public FactoryInfoPanel(FactoryBuilding building) {
        super();
        this.building = building;

        // Factory title with wrapping
        buildingLabel = createWrappedTitleLabel("Factory: " + building.getName());
        add(buildingLabel).left().width(TITLE_WIDTH).padBottom(10).row();

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

    /**
     * Update the data for the factory info panel.
     *
     * @param building the factory building to display information for
     */
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

    /**
     * Update the data for the factory info panel.
     * Override the parent class method to handle different data types.
     *
     * @param data the object containing building data
     */
    @Override
    public void updateData(Object data) {
        updateData((FactoryBuilding) data);
    }

    /**
     * Get the request policy box.
     *
     * @return the request policy box
     */
    public VisSelectBox<String> getRequestPolicyBox() {
        return requestPolicyBox;
    }

    /**
     * Get the source policy box.
     *
     * @return the source policy box
     */
    public VisSelectBox<String> getSourcePolicyBox() {
        return sourcePolicyBox;
    }

    /**
     * Get the new request button.
     *
     * @return the new request button
     */
    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }

    /**
     * Get the building.
     *
     * @return the building
     */
    public Building getBuilding() {
        return building;
    }
}
