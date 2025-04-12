package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;


import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.ui.style.UIButtonStyle;
import edu.duke.ece651.factorysim.ui.style.UISelectBoxStyle;
import edu.duke.ece651.factorysim.ui.TopBar;
import edu.duke.ece651.factorysim.ui.LogPanel;
import edu.duke.ece651.factorysim.ui.InfoPanel;
import edu.duke.ece651.factorysim.ui.ControlPanel;
import edu.duke.ece651.factorysim.util.FileDialogUtil;

import com.kotcrab.vis.ui.widget.*;

public class SimulationScreen implements Screen {
    private Stage stage;
    private FactoryGame game;
    private TopBar topBar;
    private LogPanel logPanel;
    private InfoPanel infoPanel;
    private ControlPanel controlPanel;
    private int currentStep = 0;
    private FileChooser fileChooser;

    public SimulationScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load VisUI
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        // Register custom UI styles
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();

        // Use the refactored utility method to create a FileChooser
        fileChooser = FileDialogUtil.createFileChooser(game);

        // Create the root layout
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Initialize top bar
        topBar = new TopBar(currentStep);
        topBar.getLoadButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Display file chooser
                stage.addActor(fileChooser.fadeIn());
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
            }
        });

        // Initialize other UI panels
        logPanel = new LogPanel();
        infoPanel = new InfoPanel();
        infoPanel.getNewRequestButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRequestDialog();
            }
        });

        // Initialize control panel
        controlPanel = new ControlPanel();
        controlPanel.getStepButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int stepsToMove = controlPanel.getStepCount();
                game.step(stepsToMove);
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
            }
        });

        controlPanel.getFinishButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.finish();
            }
        });


        // Set up click listener for the New Request button
        infoPanel.getNewRequestButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRequestDialog();
            }
        });

        // add all panels to root
        root.add(topBar).colspan(3).expandX().fillX().pad(10).row();
        root.add(logPanel).width(200).expandY().fillY().top().pad(10);
        root.add().expand().fill();  // center space (for map, etc.)
        root.add(infoPanel).width(200).top().pad(10).row();
        root.add().colspan(2).expandX().fillX();
        root.add(controlPanel).right().pad(10);
    }

    /**
     * Shows a dialog for creating a new request
     */
    private void showRequestDialog() {
        // create the dialog
        VisDialog dialog = new VisDialog("Request items");

        // create the dropdown for item selection
        final VisSelectBox<String> itemSelectBox = new VisSelectBox<>();
        itemSelectBox.setItems("door");

        // create a container for the dialog content
        VisTable contentTable = new VisTable();
        contentTable.pad(10);

        // create the text components
        VisLabel selectLabel = new VisLabel("Select '");
        VisLabel singleQuote = new VisLabel("'");
        VisLabel fromLabel = new VisLabel(" from 'D'"); // placeholder 'D' factory

        // add components to the dialog
        contentTable.add(selectLabel).padRight(0);
        contentTable.add(itemSelectBox).padRight(0).width(80);
        contentTable.add(singleQuote).padRight(0);
        contentTable.add(fromLabel);

        // add buttons
        dialog.getButtonsTable().defaults().pad(2, 10, 2, 10);
        dialog.button("Cancel", false);
        dialog.button("OK", true);

        // set content and configure dialog
        dialog.getContentTable().add(contentTable).pad(10);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);
        dialog.pack();
        dialog.centerWindow();

        // show the dialog
        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }
}
