package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.Item;
import edu.duke.ece651.factorysim.WasteDisposalBuilding;
import edu.duke.ece651.factorysim.Building;

/**
 * UI panel to display information about a Waste Disposal building.
 */
public class WasteDisposalInfoPanel extends InfoPanel {
    private final WasteDisposalBuilding disposal;
    private final VisLabel titleLabel;
    private final VisTable wasteInfoTable;
    private final VisLabel totalWasteLabel;

    // Width for title label
    private static final float TITLE_WIDTH = 200f;

    /**
     * Constructs the panel for a Waste Disposal building.
     *
     * @param disposal the WasteDisposalBuilding instance to display
     */
    public WasteDisposalInfoPanel(WasteDisposalBuilding disposal) {
        super();
        this.disposal = disposal;

        // Title label with wrapping
        titleLabel = createWrappedTitleLabel("Waste Disposal: " + disposal.getName());
        add(titleLabel).left().width(TITLE_WIDTH).padBottom(10).row();

        // Total waste summary label
        totalWasteLabel = new VisLabel();
        totalWasteLabel.setColor(Color.DARK_GRAY);
        add(totalWasteLabel).left().padLeft(10).padBottom(10).row();

        // Table for detailed waste type info
        wasteInfoTable = new VisTable(true);
        add(wasteInfoTable).left().padLeft(10).padTop(5).row();

        updateData();
    }

    /**
     * Updates all displayed data in the panel.
     */
    public void updateData() {
        wasteInfoTable.clearChildren();

        for (Item wasteType : disposal.getWasteTypes()) {
            int capacity = disposal.getMaxCapacityFor(wasteType);
            int rate = disposal.getDisposalRateFor(wasteType);
            int timeSteps = disposal.getDisposalTimeStepsFor(wasteType);

            VisTable block = new VisTable(false);

            VisLabel nameLabel = new VisLabel(wasteType.getName());
            nameLabel.setColor(Color.SCARLET);
            nameLabel.setFontScale(1.05f);
            block.add(nameLabel).left().padBottom(4).row();

            VisLabel capacityLabel = new VisLabel("Capacity: " + capacity);
            VisLabel rateLabel = new VisLabel("Disposal Rate: " + rate + " / step");
            VisLabel timeLabel = new VisLabel("Time to Dispose: " + timeSteps + " steps");

            for (VisLabel label : new VisLabel[]{ capacityLabel, rateLabel, timeLabel }) {
                label.setColor(Color.DARK_GRAY);
                block.add(label).left().padLeft(10).padBottom(2).row();
            }

            wasteInfoTable.add(block).left().padBottom(10).row();
        }
    }

    /**
     * Update the data for the waste disposal info panel.
     * Override the parent class method to handle different data types.
     *
     * @param data the object containing building data
     */
    @Override
    public void updateData(Object data) {
        updateData();
    }

    /**
     * Gets the WasteDisposalBuilding shown in this panel.
     *
     * @return the WasteDisposalBuilding
     */
    @Override
    public Building getBuilding() {
        return disposal;
    }
}
