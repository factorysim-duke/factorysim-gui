package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import edu.duke.ece651.factorysim.Constants;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.screen.ui.LoginDialog;
import edu.duke.ece651.factorysim.screen.ui.style.UIButtonStyle;

/**
 * This class represents the home screen of the application.
 * It provides options for login, starting a game, multiplayer, and settings.
 */
public class HomeScreen implements Screen {
    private final FactoryGame game;
    private Stage stage;
    private Texture logoTexture;

    public HomeScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        // Register custom button styles
        UIButtonStyle.registerCustomStyles();

        // Create stage
        stage = new Stage(new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Load textures
        logoTexture = new Texture("logo.png");

        // Create main layout
        setupUI();
    }

    /**
     * Setup the UI for the home screen.
     */
    private void setupUI() {
        // Create root table that fills the screen
        VisTable root = new VisTable();
        root.setFillParent(true);

        // pad left and right
        root.pad(10, 10, 10, 10);
        root.defaults().fillX().expandX();
        stage.addActor(root);

        // Add spacer at top to push content down
        root.add().expandY().height(80).row();

        // Add logo (centered)
        Image logo = new Image(logoTexture);

        float originalWidth = logoTexture.getWidth();
        float originalHeight = logoTexture.getHeight();

        float scale = 0.5f;
        logo.setSize(originalWidth * scale, originalHeight * scale);

        root.add(logo).center().size(logo.getWidth(), logo.getHeight()).row();

        // Main menu buttons (centered, stacked vertically)
        VisTable buttonTable = new VisTable();

        // Login Button
        VisTextButton loginButton = createMenuButton("Login");
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoginDialog.show(stage, new LoginDialog.LoginCallback() {
                    @Override
                    public void onLoginSuccess(String username) {
                        // Show welcome message
                        Gdx.app.log("HomeScreen", "Login successful for user: " + username);
                        VisLabel welcomeLabel = new VisLabel("Welcome, " + username + "!");
                        welcomeLabel.setPosition(20, Constants.WINDOW_HEIGHT - 40);
                        stage.addActor(welcomeLabel);
                    }

                    @Override
                    public void onLoginCancel() {
                        Gdx.app.log("HomeScreen", "Login cancelled");
                    }
                });
            }
        });
        buttonTable.add(loginButton).width(300).height(60).padBottom(20).row();

        // Start Game Button
        VisTextButton startGameButton = createMenuButton("Start Game");
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SimulationScreen(game));
                dispose();
            }
        });
        buttonTable.add(startGameButton).width(300).height(60).padBottom(20).row();

        // Multiplayer Button
        VisTextButton multiplayerButton = createMenuButton("Multiplayer");
        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO: Implement multiplayer
                // game.setScreen(new MultiplayerScreen(game));
                VisLabel multiplayerLabel = new VisLabel("Multiplayer is not available yet");
                multiplayerLabel.setPosition(20, Constants.WINDOW_HEIGHT - 60);
                // set duration to 3 seconds
                multiplayerLabel.addAction(Actions.sequence(
                    Actions.fadeIn(1f),
                    Actions.delay(3f),
                    Actions.fadeOut(1f)
                ));
                stage.addActor(multiplayerLabel);
                // dispose();
            }
        });
        buttonTable.add(multiplayerButton).width(300).height(60).padBottom(20).row();

        // Settings Button
        VisTextButton settingsButton = createMenuButton("Settings");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
                dispose();
            }
        });
        buttonTable.add(settingsButton).width(300).height(60).row();

        // Add buttons with some spacing from logo
        root.add(buttonTable).center().padTop(50).row();

        // Add expanding space to push footer to bottom
        root.add().expandY().row();

        // Footer with version and team info
        VisTable footerTable = new VisTable();

        // Version info at bottom left
        VisLabel versionLabel = new VisLabel("Version 1.0");
        footerTable.add(versionLabel).left().expandX();

        // Team info at bottom right
        VisLabel teamLabel = new VisLabel("FactorySim - Duke");
        footerTable.add(teamLabel).right();

        root.add(footerTable).fillX();
    }

    private VisTextButton createMenuButton(String text) {
        VisTextButton button = new VisTextButton(text, "blue");
        button.getLabel().setFontScale(1.3f);
        return button;
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
        if (logoTexture != null) {
            logoTexture.dispose();
        }
    }
}
