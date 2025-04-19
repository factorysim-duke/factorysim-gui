// RequestDialogManager.java
package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.FactoryBuilding;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.MineBuilding;

/**
 * Manager for request dialogs.
 */
public class RequestDialogManager {
    private final FactoryGame game;
    private final Stage stage;

    /**
     * Constructor for the RequestDialogManager class.
     * @param game the game
     * @param stage the stage
     */
    public RequestDialogManager(FactoryGame game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

    /**
     * Show the request dialog for a building.
     * @param building the building
     */
    public void showRequestDialog(Building building) {
        // Get available items for this building
        String[] items = getAvailableItems(building);
        if (items == null || items.length == 0) {
            return;
        }

        // Create the dropdown for item selection
        final VisSelectBox<String> itemSelectBox = new VisSelectBox<>();
        itemSelectBox.setItems(items);

        // Create the dialog
        VisDialog dialog = createRequestDialog(building, itemSelectBox);

        // Show the dialog
        dialog.show(stage);
    }

    /**
     * Get the available items for a building.
     * @param building the building
     * @return the available items
     */
    private String[] getAvailableItems(Building building) {
        if (building instanceof FactoryBuilding) {
            return ((FactoryBuilding) building).getFactoryType().getRecipes().stream()
                    .map(r -> r.getOutput().getName())
                    .toArray(String[]::new);
        } else if (building instanceof MineBuilding) {
            return new String[]{((MineBuilding) building).getResource().getName()};
        }
        return null;
    }

    /**
     * Create the request dialog.
     * @param building the building
     * @param itemSelectBox the item select box
     * @return the request dialog
     */
    private VisDialog createRequestDialog(Building building, VisSelectBox<String> itemSelectBox) {
        VisDialog dialog = new VisDialog("Request items") {
            @Override
            protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    String selectedItem = itemSelectBox.getSelected();
                    game.makeUserRequest(selectedItem, building.getName());
                }
                this.hide();
            }
        };

        // Create a container for the dialog content
        VisTable contentTable = new VisTable();
        contentTable.pad(10);

        // Create the text components
        VisLabel selectLabel = new VisLabel("Request '");
        VisLabel singleQuote = new VisLabel("'");
        VisLabel fromLabel = new VisLabel(" from '" + building.getName() + "'");

        // Add components to the dialog
        contentTable.add(selectLabel).padRight(0);
        contentTable.add(itemSelectBox).padRight(0).width(80);
        contentTable.add(singleQuote).padRight(0);
        contentTable.add(fromLabel);

        // Add buttons
        dialog.getButtonsTable().defaults().pad(2, 10, 2, 10);
        dialog.button("Cancel", false);
        dialog.button("OK", true);

        // Set content and configure dialog
        dialog.getContentTable().add(contentTable).pad(10);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.pack();
        dialog.centerWindow();

        return dialog;
    }
}