package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.FactoryBuilding;
import edu.duke.ece651.factorysim.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class FactoryInfoPanel extends InfoPanel {
    private final VisLabel buildingLabel;
    private final VisLabel outputsLabel;
    private final VisLabel sourcesLabel;
    private final VisLabel queueLabel;

    private final VisLabel policyLabel;
    private final VisSelectBox<String> policyBox;
    private final VisTextButton newRequestButton;

    public FactoryInfoPanel(FactoryBuilding building) {
        super();
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.98f, 0.98f, 0.98f, 0.95f)));
        pad(15);
        top().left();

        // Factory title
        buildingLabel = new VisLabel("Factory: " + building.getName());
        buildingLabel.setColor(Color.BLACK);
        buildingLabel.setFontScale(1.2f);
        add(buildingLabel).left().padBottom(10).row();

        // Request policy 行
        policyLabel = new VisLabel("Request Policy:");
        policyLabel.setColor(Color.BLACK);
        policyBox = new VisSelectBox<>("blue");
        policyBox.setItems("FIFO", "READY", "SJF");

        VisTable policyRow = new VisTable(true); // true = spacing between columns
        policyRow.add(policyLabel).left();
        policyRow.add(policyBox).width(100).left();
        add(policyRow).left().padBottom(10).row();

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

    public VisSelectBox<String> getPolicyBox() {
        return policyBox;
    }

    public VisTextButton getNewRequestButton() {
        return newRequestButton;
    }
}
