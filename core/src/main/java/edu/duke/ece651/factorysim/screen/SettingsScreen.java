package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.Constants;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.Tuple;

/**
 * This class represents the settings screen of the application.
 * It provides options for adjusting game settings.
 */
public class SettingsScreen implements Screen {
    private final FactoryGame game;
    private Stage stage;

    // Settings options
    private VisSelectBox<String> mapSizeSelectBox;

    // Host and port fields
    private VisTextField hostField;
    private VisTextField portField;

    // Server preset field
    private VisTextField presetField;

    // Map size options
    private final String[] mapSizeOptions = {
        "Tiny (16x9 grid)",
        "Small (32x18 grid)",
        "Medium (64x36 grid)",
        "Large (96x54 grid)",
        "Extra Large (128x72 grid)"
    };

    // Grid dimensions (columns x rows)
    private final int[][] gridDimensions = {
        {16, 9},   // Tiny
        {32, 18},  // Small
        {64, 36},  // Medium (default)
        {96, 54},  // Large
        {128, 72}   // Extra Large
    };

    // Default value index
    private int defaultSizeIndex = 2; // Medium by default

    // Preferences key
    public static final String PREFS_NAME = "factorysim";
    public static final String GRID_COLS_KEY = "grid_cols";
    public static final String GRID_ROWS_KEY = "grid_rows";
    public static final String HOST_KEY = "server_host";
    public static final String PORT_KEY = "server_port";
    public static final String PRESET_KEY = "server_preset";

    public SettingsScreen(FactoryGame game) {
        this.game = game;
        // Load existing preferences if available
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (prefs.contains(GRID_COLS_KEY)) {
            int savedCols = prefs.getInteger(GRID_COLS_KEY);
            // Find the closest map size
            defaultSizeIndex = findClosestGridSizeIndex(savedCols);
        }
    }

    private int findClosestGridSizeIndex(int cols) {
        int closestIndex = 0;
        int minDifference = Math.abs(gridDimensions[0][0] - cols);

        for (int i = 1; i < gridDimensions.length; i++) {
            int difference = Math.abs(gridDimensions[i][0] - cols);
            if (difference < minDifference) {
                minDifference = difference;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        // Create stage
        stage = new Stage(new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        setupUI();
    }

    private void setupUI() {
        // Create root table that fills the screen
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        VisLabel titleLabel = new VisLabel("Settings");
        titleLabel.setFontScale(2.0f);
        root.add(titleLabel).padTop(50).padBottom(40).row();

        // Settings panel
        VisTable settingsTable = new VisTable();
        settingsTable.setBackground(VisUI.getSkin().getDrawable("window"));
        settingsTable.pad(20);

        // Map Settings
        VisLabel mapLabel = new VisLabel("Game World Size");
        mapLabel.setFontScale(1.5f);
        settingsTable.add(mapLabel).colspan(2).left().padBottom(20).row();

        // Map size dropdown
        settingsTable.add(new VisLabel("Map Size:")).left().padRight(10);
        mapSizeSelectBox = new VisSelectBox<>();
        mapSizeSelectBox.setItems(mapSizeOptions);
        mapSizeSelectBox.setSelectedIndex(defaultSizeIndex);
        settingsTable.add(mapSizeSelectBox).expandX().fillX().padBottom(10).row();

        // Host input
        settingsTable.add(new VisLabel("Host:")).left().padRight(10);
        hostField = new VisTextField();
        settingsTable.add(hostField).expandX().fillX().padBottom(10).row();

        // Port input
        settingsTable.add(new VisLabel("Port:")).left().padRight(10);
        portField = new VisTextField();
        settingsTable.add(portField).expandX().fillX().padBottom(10).row();

        // Set default host and port inputs
        Tuple<String, Integer> hostAndPort = getStoredHostAndPort();
        hostField.setText(hostAndPort.first());
        portField.setText(Integer.toString(hostAndPort.second()));

        // Preset input
        settingsTable.add(new VisLabel("Preset:")).left().padRight(10);
        presetField = new VisTextField();
        settingsTable.add(presetField).expandX().fillX().padBottom(10).row();
        presetField.setText(getStoredPreset());

        // Buttons
        VisTextButton backButton = new VisTextButton("Back", "blue");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to home screen
                game.setScreen(new HomeScreen(game));
                dispose();
            }
        });

        VisTextButton saveButton = new VisTextButton("Save", "blue");
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
                // Return to home screen
                game.setScreen(new HomeScreen(game));
                dispose();
            }
        });

        VisTable buttonTable = new VisTable();
        buttonTable.add(backButton).padRight(10);
        buttonTable.add(saveButton);

        settingsTable.add(buttonTable).colspan(2).right().padTop(20);

        root.add(settingsTable).width(500).row();
    }

    private void saveSettings() {
        int selectedIndex = mapSizeSelectBox.getSelectedIndex();
        int cols = gridDimensions[selectedIndex][0];
        int rows = gridDimensions[selectedIndex][1];

        // Save to preferences
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(GRID_COLS_KEY, cols);
        prefs.putInteger(GRID_ROWS_KEY, rows);
        prefs.putString(HOST_KEY, hostField.getText());
        prefs.putInteger(PORT_KEY, Integer.parseInt(portField.getText()));
        prefs.putString(PRESET_KEY, presetField.getText());
        prefs.flush();

        Gdx.app.log("SettingsScreen",
            String.format("Settings saved: grid=%dx%d", cols, rows));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        // Don't dispose VisUI here as other screens may use it
    }

    /**
     * Utility method to get the stored grid dimensions.
     * Can be called from other screens.
     */
    public static int[] getStoredGridDimensions() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        int defaultCols = 40; // Medium size default
        int defaultRows = 24;

        if (prefs.contains(GRID_COLS_KEY) && prefs.contains(GRID_ROWS_KEY)) {
            return new int[] {
                prefs.getInteger(GRID_COLS_KEY, defaultCols),
                prefs.getInteger(GRID_ROWS_KEY, defaultRows)
            };
        }

        return new int[] {defaultCols, defaultRows};
    }

    public static Tuple<String, Integer> getStoredHostAndPort() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String host = prefs.getString(HOST_KEY, "localhost");
        int port = prefs.getInteger(PORT_KEY, 6510);
        return new Tuple<>(host, port);
    }

    public static String getStoredPreset() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getString(PRESET_KEY, "");
    }
}
