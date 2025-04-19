package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

import edu.duke.ece651.factorysim.GameWorld;
import edu.duke.ece651.factorysim.Item;
import edu.duke.ece651.factorysim.Recipe;
import edu.duke.ece651.factorysim.Type;

import java.util.HashMap;
import java.util.List;

/**
 * A panel containing building buttons for the simulation.
 */
public class BuildingButtonsPanel extends VisTable {
    private final GameWorld gameWorld;
    private final Texture selectTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final ButtonGroup<VisImageButton> buttonGroup;

    private VisImageButton defaultButton;
    private VisImageButton mineButton;
    private VisImageButton factoryButton;
    private VisImageButton storageButton;
    private VisImageButton connectButton;

    /**
     * Constructor for the BuildingButtonsPanel.
     *
     * @param gameWorld is the game world to operate on
     * @param selectTexture is the texture for selection
     * @param mineTexture is the texture for mining buildings
     * @param factoryTexture is the texture for factory buildings
     * @param storageTexture is the texture for storage buildings
     */
    public BuildingButtonsPanel(GameWorld gameWorld,
                               Texture selectTexture,
                               Texture mineTexture,
                               Texture factoryTexture,
                               Texture storageTexture) {
        this.gameWorld = gameWorld;
        this.selectTexture = selectTexture;
        this.mineTexture = mineTexture;
        this.factoryTexture = factoryTexture;
        this.storageTexture = storageTexture;
        this.buttonGroup = new ButtonGroup<>();

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
        mineButton = createImageButton(mineTexture);
        factoryButton = createImageButton(factoryTexture);
        storageButton = createImageButton(storageTexture);
        connectButton = createImageButton(selectTexture); // Using selectTexture for connect button

        // Add buttons to button group for exclusive selection
        buttonGroup.add(defaultButton);
        buttonGroup.add(mineButton);
        buttonGroup.add(factoryButton);
        buttonGroup.add(storageButton);
        buttonGroup.add(connectButton);

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
        add(mineButton).pad(5);
        add(factoryButton).pad(5);
        add(storageButton).pad(5);
        add(connectButton).pad(5).row();

        // Add labels under buttons
        add("Select").pad(2);
        add("Mine").pad(2);
        add("Factory").pad(2);
        add("Storage").pad(2);
        add("Connect").pad(2);
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

        // Mine building tool
        mineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: In a real implementation, show a dialog to select recipe
                Recipe miningRecipe = new Recipe(new Item("metal"), new HashMap<>(), 1);
                gameWorld.enterBuildMinePhase("Mine", miningRecipe);
            }
        });

        // Factory building tool
        factoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: In a real implementation, show a dialog to select factory type
                Type factoryType = new Type("hinge", List.of());
                gameWorld.enterBuildFactoryPhase("Factory", factoryType);
            }
        });

        // Storage building tool
        storageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: In a real implementation, show a dialog to select item, capacity and priority
                gameWorld.enterBuildStoragePhase("Storage", new Item("metal"), 10, 1.0);
            }
        });

        // Connect buildings tool
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.enterConnectPhase();
            }
        });
    }
}