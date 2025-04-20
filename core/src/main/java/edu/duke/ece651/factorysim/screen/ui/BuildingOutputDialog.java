package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;

import edu.duke.ece651.factorysim.GameWorld;
import edu.duke.ece651.factorysim.Item;
import edu.duke.ece651.factorysim.Recipe;
import edu.duke.ece651.factorysim.Type;
import edu.duke.ece651.factorysim.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Dialog for selecting building outputs (recipes or factory types) when placing buildings.
 */
public class BuildingOutputDialog {
    private final Stage stage;
    private final GameWorld gameWorld;

    /**
     * Constructor for the BuildingOutputDialog class.
     *
     * @param stage the stage to show the dialog on
     * @param gameWorld the game world instance
     */
    public BuildingOutputDialog(Stage stage, GameWorld gameWorld) {
        this.stage = stage;
        this.gameWorld = gameWorld;
    }

    /**
     * Get all available resources from recipes in the world
     *
     * @return array of resource names
     */
    private String[] getAvailableResources() {
        World world = gameWorld.getSim().getWorld();
        Set<String> resources = new HashSet<>();

        // Get all recipe outputs from the world as potential resources
        for (Recipe recipe : world.getRecipes()) {
            if (recipe.getIngredients().isEmpty()) {
                // Recipes with no ingredients are resources
                resources.add(recipe.getOutput().getName());
            }
        }

        // If no resources found in the world, create a wrong resource
        if (resources.isEmpty()) {
            resources.add("wrong");
        }

        return resources.toArray(new String[0]);
    }

    /**
     * Get all available factory types from the world
     *
     * @return array of factory type names
     */
    private String[] getAvailableFactoryTypes() {
        World world = gameWorld.getSim().getWorld();
        List<Type> types = world.getTypes();
        List<String> typeNames = new ArrayList<>();

        // Get all type names from the world
        for (Type type : types) {
            typeNames.add(type.getName());
        }

        // If no types found in the world, create a wrong type
        if (typeNames.isEmpty()) {
            typeNames.add("wrong");
        }

        return typeNames.toArray(new String[0]);
    }

    /**
     * Get all available item types that can be stored (combine resources and outputs)
     *
     * @return array of item names
     */
    private String[] getAvailableItems() {
        World world = gameWorld.getSim().getWorld();
        Set<String> items = new HashSet<>();

        // Add all recipe outputs as potential storage items
        for (Recipe recipe : world.getRecipes()) {
            items.add(recipe.getOutput().getName());
        }

        // If no items found in the world, create a wrong item
        if (items.isEmpty()) {
            items.add("wrong");
        }

        return items.toArray(new String[0]);
    }

    /**
     * Shows a dialog for selecting a mine output (resource).
     *
     * @param onSelect callback for when a selection is made
     */
    public void showMineOutputDialog(Consumer<Recipe> onSelect) {
        // Get available resources
        String[] resources = getAvailableResources();

        final VisSelectBox<String> resourceSelect = new VisSelectBox<>();
        resourceSelect.setItems(resources);

        VisDialog dialog = new VisDialog("Select Mine Output") {
            @Override
            protected void result(Object result) {
                if (Boolean.TRUE.equals(result)) {
                    String selectedResource = resourceSelect.getSelected();

                    // Try to find the existing recipe for this resource
                    World world = gameWorld.getSim().getWorld();
                    Recipe miningRecipe = null;

                    for (Recipe recipe : world.getRecipes()) {
                        if (recipe.getOutput().getName().equals(selectedResource) &&
                            recipe.getIngredients().isEmpty()) {
                            miningRecipe = recipe;
                            break;
                        }
                    }

                    // If not found, create a simple mining recipe
                    if (miningRecipe == null) {
                        miningRecipe = new Recipe(new Item(selectedResource), new HashMap<>(), 1);
                    }

                    onSelect.accept(miningRecipe);
                }
            }
        };

        VisTable contentTable = new VisTable(true);
        contentTable.add(new VisLabel("Resource to mine:")).padRight(10);
        contentTable.add(resourceSelect).growX();

        dialog.getContentTable().add(contentTable).pad(10).growX();

        // Add buttons
        dialog.button("Cancel", false);
        dialog.button("OK", true);

        dialog.setModal(true);
        dialog.setMovable(true);
        dialog.setResizable(false);
        dialog.pack();
        dialog.centerWindow();
        dialog.show(stage);
    }

    /**
     * Shows a dialog for selecting a factory output (factory type).
     *
     * @param onSelect callback for when a selection is made
     */
    public void showFactoryOutputDialog(Consumer<Type> onSelect) {
        // Get available factory types
        String[] factoryTypes = getAvailableFactoryTypes();

        final VisSelectBox<String> typeSelect = new VisSelectBox<>();
        typeSelect.setItems(factoryTypes);

        VisDialog dialog = new VisDialog("Select Factory Output") {
            @Override
            protected void result(Object result) {
                if (Boolean.TRUE.equals(result)) {
                    String selectedType = typeSelect.getSelected();

                    // Try to find the existing type definition
                    World world = gameWorld.getSim().getWorld();
                    Type selectedFactoryType = null;

                    for (Type type : world.getTypes()) {
                        if (type.getName().equals(selectedType)) {
                            selectedFactoryType = type;
                            break;
                        }
                    }

                    // If not found, create a new empty type - shouldn't happen if types are properly loaded
                    if (selectedFactoryType == null) {
                        List<Recipe> typeRecipes = new ArrayList<>();
                        // Try to find recipes for this type
                        for (Recipe recipe : world.getRecipes()) {
                            if (recipe.getOutput().getName().equals(selectedType)) {
                                typeRecipes.add(recipe);
                            }
                        }
                        selectedFactoryType = new Type(selectedType, typeRecipes);
                    }

                    onSelect.accept(selectedFactoryType);
                }
            }
        };

        VisTable contentTable = new VisTable(true);
        contentTable.add(new VisLabel("Factory output:")).padRight(10);
        contentTable.add(typeSelect).growX();

        dialog.getContentTable().add(contentTable).pad(10).growX();

        // Add buttons
        dialog.button("Cancel", false);
        dialog.button("OK", true);

        dialog.setModal(true);
        dialog.setMovable(true);
        dialog.setResizable(false);
        dialog.pack();
        dialog.centerWindow();
        dialog.show(stage);
    }

    /**
     * Shows a dialog for selecting a storage building configuration.
     *
     * @param onSelect callback for when a selection is made with parameters: item, capacity, priority
     */
    public void showStorageOutputDialog(Consumer<StorageConfig> onSelect) {
        // Get available items for storage
        String[] itemTypes = getAvailableItems();

        final VisSelectBox<String> itemSelect = new VisSelectBox<>();
        itemSelect.setItems(itemTypes);

        VisDialog dialog = new VisDialog("Configure Storage") {
            @Override
            protected void result(Object result) {
                if (Boolean.TRUE.equals(result)) {
                    String selectedItem = itemSelect.getSelected();
                    // Default values for capacity and priority
                    int capacity = 10;
                    double priority = 1.0;

                    // Create storage configuration
                    StorageConfig config = new StorageConfig(
                        new Item(selectedItem),
                        capacity,
                        priority
                    );
                    onSelect.accept(config);
                }
            }
        };

        // In a more complete implementation, you'd add fields for capacity and priority

        VisTable contentTable = new VisTable(true);
        contentTable.add(new VisLabel("Store item:")).padRight(10);
        contentTable.add(itemSelect).growX();

        dialog.getContentTable().add(contentTable).pad(10).growX();

        // Add buttons
        dialog.button("Cancel", false);
        dialog.button("OK", true);

        dialog.setModal(true);
        dialog.setMovable(true);
        dialog.setResizable(false);
        dialog.pack();
        dialog.centerWindow();
        dialog.show(stage);
    }

    /**
     * Helper class to hold storage configuration parameters.
     */
    public static class StorageConfig {
        private final Item item;
        private final int capacity;
        private final double priority;

        public StorageConfig(Item item, int capacity, double priority) {
            this.item = item;
            this.capacity = capacity;
            this.priority = priority;
        }

        public Item getItem() {
            return item;
        }

        public int getCapacity() {
            return capacity;
        }

        public double getPriority() {
            return priority;
        }
    }
}
