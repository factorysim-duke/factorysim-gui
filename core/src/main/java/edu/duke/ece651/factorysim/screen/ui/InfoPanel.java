package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import edu.duke.ece651.factorysim.Building;

/**
 * Generic panel base class.
 */
public class InfoPanel extends VisTable {

    /**
     * Constructor for the InfoPanel class.
     */
    public InfoPanel() {
        super();
        setVisible(true);
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        pad(15);
        top().left();
    }

    /**
     * Update building information in the panel.
     * This method should be overridden by subclasses to update their
     * specific UI components with the latest building data.
     *
     * @param data The building data to update with (can be Building or specific building type)
     */
    public void updateData(Object data) {
    }

    /**
     * Update building information with a specific Building instance.
     * This is a convenience method for type safety.
     *
     * @param building The building to update with
     */
    public void updateData(Building building) {
        updateData((Object)building);
    }

    /**
     * Get the building associated with this info panel.
     *
     * @return The building or null if not applicable
     */
    public Building getBuilding() {
        return null;
    }

    /**
     * Get the new request button.
     *
     * @return the new request button
     */
    public VisTextButton getNewRequestButton() {
        return null;
    }

    /**
     * Creates a title label with proper text wrapping configuration.
     *
     * @param text The title text
     * @return A properly configured VisLabel with text wrapping
     */
    protected VisLabel createWrappedTitleLabel(String text) {
        VisLabel label = new VisLabel(text);
        label.setColor(Color.BLACK);
        label.setFontScale(1.2f);
        label.setWrap(true);
        label.setAlignment(Align.left);
        return label;
    }
}
