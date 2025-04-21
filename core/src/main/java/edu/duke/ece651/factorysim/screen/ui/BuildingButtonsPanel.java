package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.*;

/**
 * A panel containing building buttons for the simulation.
 */
public class BuildingButtonsPanel extends VisTable {
    private final GameWorld gameWorld;
    private final Texture selectTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final Texture dronePortTexture;
    private final ButtonGroup<VisImageButton> buttonGroup;
    private final BuildingOutputDialog outputDialog;
    private final Stage stage;

    private VisImageButton defaultButton;
    private VisImageButton connectButton;
    private VisImageButton mineButton;
    private VisImageButton factoryButton;
    private VisImageButton storageButton;
    private VisImageButton dronePortButton;

    /**
     * Constructor for the BuildingButtonsPanel.
     *
     * @param gameWorld is the game world to operate on
     * @param stage is the stage to display dialogs on
     * @param selectTexture is the texture for selection
     * @param mineTexture is the texture for mining buildings
     * @param factoryTexture is the texture for factory buildings
     * @param storageTexture is the texture for storage buildings
     * @param dronePortTexture is the texture for drone port buildings
     */
    public BuildingButtonsPanel(GameWorld gameWorld,
                               Stage stage,
                               Texture selectTexture,
                               Texture mineTexture,
                               Texture factoryTexture,
                               Texture storageTexture,
                               Texture dronePortTexture) {
        this.gameWorld = gameWorld;
        this.stage = stage;
        this.selectTexture = selectTexture;
        this.mineTexture = mineTexture;
        this.factoryTexture = factoryTexture;
        this.storageTexture = storageTexture;
        this.dronePortTexture = dronePortTexture;
        this.buttonGroup = new ButtonGroup<>();
        this.outputDialog = new BuildingOutputDialog(stage, gameWorld);

        createButtons();
        setupLayout();
        attachListeners();
    }

    /**
     * Create buttons for the panel.
     */
    private void createButtons() {
        // Create buttons using the textures
        defaultButton = createImageButton(selectTexture);
        connectButton = createImageButton(selectTexture); // Using selectTexture for connect button
        mineButton = createImageButton(mineTexture);
        factoryButton = createImageButton(factoryTexture);
        storageButton = createImageButton(storageTexture);
        dronePortButton = createImageButton(dronePortTexture);

        // Add buttons to button group for exclusive selection
        buttonGroup.add(defaultButton);
        buttonGroup.add(connectButton);
        buttonGroup.add(mineButton);
        buttonGroup.add(factoryButton);
        buttonGroup.add(storageButton);
        buttonGroup.add(dronePortButton);

        // Make default button checked initially
        defaultButton.setChecked(true);
    }

    /**
     * Create an image button from a texture.
     *
     * @param texture is the texture for the button
     * @return a new VisImageButton
     */
    private VisImageButton createImageButton(Texture texture) {
        TextureRegion region = new TextureRegion(texture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        VisImageButton button = new VisImageButton(drawable);
        button.setFocusBorderEnabled(true);
        button.pad(5);
        return button;
    }

    /**
     * Setup the panel layout.
     */
    private void setupLayout() {
        // Add a label above the buttons
        add("Building Tools").colspan(5).padBottom(10).row();

        // Add buttons side by side
        add(defaultButton).pad(5);
        add(connectButton).pad(5);
        add(mineButton).pad(5);
        add(factoryButton).pad(5);
        add(storageButton).pad(5);
        add(dronePortButton).row();

        // Add labels under buttons
        add("Select").pad(2);
        add("Connect").pad(2);
        add("Mine").pad(2);
        add("Factory").pad(2);
        add("Storage").pad(2);
        add("Drone Port").pad(2);
    }

    /**
     * Attach listeners to the buttons.
     */
    private void attachListeners() {
        // Default selection tool
        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.enterDefaultPhase();
            }
        });

        // Connect buildings tool
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.enterConnectPhase();
            }
        });

        // Mine building tool
        mineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Skip if dialog is open
                if (isDialogOpen()) {
                    return;
                }

                // Show dialog to select mine output
                outputDialog.showMineOutputDialog(miningRecipe -> {
                    // Use the output resource name as the building name
                    String buildingName = miningRecipe.getOutput().getName() + "_Mine";
                    gameWorld.enterBuildMinePhase(buildingName, miningRecipe);
                });
            }
        });

        // Factory building tool
        factoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Skip if dialog is open
                if (isDialogOpen()) {
                    return;
                }

                // Show dialog to select factory type
                outputDialog.showFactoryOutputDialog(factoryType -> {
                    // Use the type name as the building name
                    String buildingName = factoryType.getName() + "_Factory";
                    gameWorld.enterBuildFactoryPhase(buildingName, factoryType);
                });
            }
        });

        // Storage building tool
        storageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Skip if dialog is open
                if (isDialogOpen()) {
                    return;
                }

                // Show dialog to select storage configuration
                outputDialog.showStorageOutputDialog(config -> {
                    // Use the item name as the building name
                    String buildingName = config.getItem().getName() + "_Storage";
                    gameWorld.enterBuildStoragePhase(
                        buildingName,
                        config.getItem(),
                        config.getCapacity(),
                        config.getPriority()
                    );
                });
            }
        });

        // Drone port building tool
        dronePortButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.enterBuildDronePortPhase("DronePort");
            }
        });

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return switch (keycode) {
                    case Input.Keys.NUM_1 -> {
                        simulateClick(defaultButton);
                        yield true;
                    }
                    case Input.Keys.NUM_2 -> {
                        simulateClick(connectButton);
                        yield true;
                    }
                    case Input.Keys.NUM_3 -> {
                        simulateClick(mineButton);
                        yield true;
                    }
                    case Input.Keys.NUM_4 -> {
                        simulateClick(factoryButton);
                        yield true;
                    }
                    case Input.Keys.NUM_5 -> {
                        simulateClick(storageButton);
                        yield true;
                    }
                    case Input.Keys.NUM_6 -> {
                        simulateClick(dronePortButton);
                        yield true;
                    }
                    default -> false;
                };
            }
        });
    }

    private boolean isDialogOpen() {
        for (Actor actor : stage.getRoot().getChildren()) {
            if (actor instanceof VisDialog && actor.isVisible()) {
                return true;
            }
        }
        return false;
    }

    private void simulateClick(VisImageButton button) {
        InputEvent down = new InputEvent();
        down.setType(InputEvent.Type.touchDown);
        down.setStage(stage);
        button.fire(down);

        InputEvent up = new InputEvent();
        up.setType(InputEvent.Type.touchUp);
        up.setStage(stage);
        button.fire(up);
    }
}
