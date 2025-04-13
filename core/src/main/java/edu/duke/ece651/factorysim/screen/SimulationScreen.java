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
import edu.duke.ece651.factorysim.util.PanelLogger;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.ui.BuildingInfoPanelFactory;
import edu.duke.ece651.factorysim.ui.FactoryInfoPanel;

public class SimulationScreen implements Screen {
    private Stage stage;
    private FactoryGame game;
    private TopBar topBar;
    private LogPanel logPanel;
    private VisTable infoPanelContainer;
    private InfoPanel currentInfoPanel;
    private ControlPanel controlPanel;
    private int currentStep = 0;
    private FileChooser createFileChooser;
    private FileChooser saveFileChooser;
    public SimulationScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        game.addInputProcessor(stage);
        // Gdx.input.setInputProcessor(stage);

        // Load VisUI
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        // Register custom UI styles
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();

        // Use the refactored utility method to create a FileChooser
        createFileChooser = FileDialogUtil.createFileChooser(game);
        saveFileChooser = FileDialogUtil.saveFileChooser(game);

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
                stage.addActor(createFileChooser.fadeIn());
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
            }
        });

        topBar.getSaveButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(saveFileChooser.fadeIn());
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
            }
        });

        // Initialize other UI panels
        logPanel = new LogPanel();
        logPanel.getVerbosityBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setVerbosity(Integer.parseInt(logPanel.getVerbosityBox().getSelected()));
            }
        });
        game.setLogger(new PanelLogger(logPanel));

        // Initialize info panel
        infoPanelContainer = new VisTable();
        infoPanelContainer.setVisible(false);

        // Initialize control panel
        controlPanel = new ControlPanel();
        controlPanel.getStepButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int stepsToMove = controlPanel.getStepCount();
                game.step(stepsToMove);
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
                // TODO: hide info panel test, remove later
                hideInfoPanel();
            }
        });

        controlPanel.getFinishButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.finish();
                currentStep = game.getCurrentStep();
                topBar.updateStepCount(currentStep);
            }
        });

        // add all panels to root
        root.add(topBar).colspan(3).expandX().fillX().pad(10).row();
        root.add(logPanel).width(200).height(Gdx.graphics.getHeight() * 0.6f).top().pad(10);
        root.add().expand().fill();  // center space (for map, etc.)

        VisTable rightCol = new VisTable();
        rightCol.top().padTop(10).padRight(10);
        rightCol.add(infoPanelContainer).top().width(220);

        root.add(rightCol).width(240).top().padTop(10).padRight(10).padBottom(10).expandY().fillY().row();

        root.add().colspan(2).expand();
        root.add(controlPanel).bottom().right().pad(10);
    }

    public void showBuildingInfo(Building building) {
        if (currentInfoPanel != null) {
            currentInfoPanel.remove();
        }

        currentInfoPanel = BuildingInfoPanelFactory.createInfoPanel(building);
        infoPanelContainer.clear();
        infoPanelContainer.add(currentInfoPanel).expand().fill().top().left().pad(5);
        infoPanelContainer.setVisible(true);

        if (currentInfoPanel instanceof FactoryInfoPanel) {
            ((FactoryInfoPanel) currentInfoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showRequestDialog();
                }
            });

            ((FactoryInfoPanel) currentInfoPanel).getRequestPolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((FactoryInfoPanel) currentInfoPanel).getRequestPolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    game.setPolicy("request", selectedPolicy, buildingName);
                    System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
                }
            });

            ((FactoryInfoPanel) currentInfoPanel).getSourcePolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((FactoryInfoPanel) currentInfoPanel).getSourcePolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    game.setPolicy("source", selectedPolicy, buildingName);
                    System.out.println("Setting source policy to " + selectedPolicy + " for building " + buildingName);
                }
            });
        }

    }

    public void hideInfoPanel() {
        infoPanelContainer.setVisible(false);
    }


    /**
     * Shows a dialog for creating a new request
     */
    private void showRequestDialog() {
        // create the dropdown for item selection
        final VisSelectBox<String> itemSelectBox = new VisSelectBox<>();
        itemSelectBox.setItems("door");

        // create the dialog
        VisDialog dialog = new VisDialog("Request items") {
            @Override
            protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    String selectedItem = itemSelectBox.getSelected();
                    game.makeUserRequest(selectedItem, "D");
                }
                this.hide();
            }
        };

        // create a container for the dialog content
        VisTable contentTable = new VisTable();
        contentTable.pad(10);

        // create the text components
        VisLabel selectLabel = new VisLabel("Select '");
        VisLabel singleQuote = new VisLabel("'");
        VisLabel fromLabel = new VisLabel(" from 'D'"); // building D is hardcoded here

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
//        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
