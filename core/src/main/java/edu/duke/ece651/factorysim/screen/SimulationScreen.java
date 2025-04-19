package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import edu.duke.ece651.factorysim.*;
import edu.duke.ece651.factorysim.ui.style.*;
import edu.duke.ece651.factorysim.ui.*;
import edu.duke.ece651.factorysim.util.*;
import com.kotcrab.vis.ui.widget.*;

public class SimulationScreen implements Screen {
    private GameWorld world;

    private Stage stage;
    private TopBar topBar;
    private LogPanel logPanel;
    private VisTable infoPanelContainer;
    private InfoPanel currentInfoPanel;
    private ControlPanel controlPanel;
    private int currentStep = 0;
    private FileChooser createFileChooser;
    private FileChooser saveFileChooser;
    private RealTimeMenu realTimeMenu;

    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Create stage
        stage = new Stage(new ScreenViewport());
        inputMultiplexer.addProcessor(stage);

        // Create game world
        int cols = Math.ceilDiv(Constants.VIEW_WIDTH, Constants.CELL_SIZE);
        int rows = Math.ceilDiv(Constants.VIEW_HEIGHT, Constants.CELL_SIZE);
        this.world = new GameWorld(cols, rows, Constants.CELL_SIZE, new StreamLogger(System.out), this,
            0f, 0f);
        inputMultiplexer.addProcessor(this.world);

        // Load VisUI
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        // Register custom UI styles
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();

        // Use the refactored utility method to create a FileChooser
        createFileChooser = FileDialogUtil.createFileChooser(this);
        saveFileChooser = FileDialogUtil.saveFileChooser(this);

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
                currentStep = world.getSim().getCurrentTime();
                topBar.updateStepCount(currentStep);
            }
        });

        topBar.getSaveButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(saveFileChooser.fadeIn());
                currentStep = world.getSim().getCurrentTime();
                topBar.updateStepCount(currentStep);
            }
        });

        // Initialize real-time menu
        realTimeMenu = new RealTimeMenu(this);

        // Create new Real-time button for bottom left corner
        VisTextButton realTimeButton = new VisTextButton("Real-time", "blue");
        realTimeButton.pad(5, 10, 5, 10);

        // Add listener for real-time button
        realTimeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Update button state to ensure it's showing the correct text
                realTimeMenu.updateButtonState();
                // Show dropdown at the bottom left position
                realTimeMenu.showMenu(stage, realTimeButton);
            }
        });

        // Initialize other UI panels
        logPanel = new LogPanel();
        logPanel.getVerbosityBox().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVerbosity(Integer.parseInt(logPanel.getVerbosityBox().getSelected()));
            }
        });
        this.world.setLogger(new PanelLogger(logPanel));

        // Initialize info panel
        infoPanelContainer = new VisTable();
        infoPanelContainer.setVisible(false);

        // Initialize control panel
        controlPanel = new ControlPanel();
        controlPanel.getStepButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int stepsToMove = controlPanel.getStepCount();
                step(stepsToMove);
                currentStep = world.getSim().getCurrentTime();
                topBar.updateStepCount(currentStep);
                // TODO: hide info panel test, remove later
                hideInfoPanel();
            }
        });

        controlPanel.getFinishButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.getSim().finish();
                currentStep = world.getSim().getCurrentTime();
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

        // Add real-time button to bottom left
        root.add(realTimeButton).left().padLeft(65).padBottom(160);
        root.add().expand();
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
                    setPolicy("request", selectedPolicy, buildingName);
                    System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
                }
            });

            ((FactoryInfoPanel) currentInfoPanel).getSourcePolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((FactoryInfoPanel) currentInfoPanel).getSourcePolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    setPolicy("source", selectedPolicy, buildingName);
                    System.out.println("Setting source policy to " + selectedPolicy + " for building " + buildingName);
                }
            });
        } else if (currentInfoPanel instanceof MineInfoPanel) {
            ((MineInfoPanel) currentInfoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showRequestDialog();
                }
            });

            ((MineInfoPanel) currentInfoPanel).getRequestPolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((MineInfoPanel) currentInfoPanel).getRequestPolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    setPolicy("request", selectedPolicy, buildingName);
                    System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
                }
            });

            ((MineInfoPanel) currentInfoPanel).getSourcePolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((MineInfoPanel) currentInfoPanel).getSourcePolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    setPolicy("source", selectedPolicy, buildingName);
                    System.out.println("Setting source policy to " + selectedPolicy + " for building " + buildingName);
                }
            });
        } else if (currentInfoPanel instanceof StorageInfoPanel) {
            ((StorageInfoPanel) currentInfoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showRequestDialog();
                }
            });

            ((StorageInfoPanel) currentInfoPanel).getRequestPolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((StorageInfoPanel) currentInfoPanel).getRequestPolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    setPolicy("request", selectedPolicy, buildingName);
                    System.out.println("Setting policy to " + selectedPolicy + " for building " + buildingName);
                }
            });

            ((StorageInfoPanel) currentInfoPanel).getSourcePolicyBox().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String selectedPolicy = ((StorageInfoPanel) currentInfoPanel).getSourcePolicyBox().getSelected().toLowerCase();
                    String buildingName = building.getName();
                    setPolicy("source", selectedPolicy, buildingName);
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
        if (currentInfoPanel == null) {
            return;
        }

        final Building currentBuilding;
        if (currentInfoPanel instanceof FactoryInfoPanel) {
            currentBuilding = ((FactoryInfoPanel) currentInfoPanel).getBuilding();
        } else if (currentInfoPanel instanceof MineInfoPanel) {
            currentBuilding = ((MineInfoPanel) currentInfoPanel).getBuilding();
        } else if (currentInfoPanel instanceof StorageInfoPanel) {
            currentBuilding = ((StorageInfoPanel) currentInfoPanel).getBuilding();
        } else {
            return;
        }

        // create the dropdown for item selection
        final VisSelectBox<String> itemSelectBox = new VisSelectBox<>();
        String[] items;
        if (currentBuilding instanceof FactoryBuilding) {
            items = ((FactoryBuilding) currentBuilding).getFactoryType().getRecipes().stream()
                    .map(r -> r.getOutput().getName())
                    .toArray(String[]::new);
        } else if (currentBuilding instanceof MineBuilding) {
            items = new String[]{((MineBuilding) currentBuilding).getResource().getName()};
        } else {
            return;
        }
        itemSelectBox.setItems(items);

        // create the dialog
        VisDialog dialog = new VisDialog("Request items") {
            @Override
            protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    String selectedItem = itemSelectBox.getSelected();
                    makeUserRequest(selectedItem, currentBuilding.getName());
                }
                this.hide();
            }
        };

        // create a container for the dialog content
        VisTable contentTable = new VisTable();
        contentTable.pad(10);

        // create the text components
        VisLabel selectLabel = new VisLabel("Request '");
        VisLabel singleQuote = new VisLabel("'");
        VisLabel fromLabel = new VisLabel(" from '" + currentBuilding.getName() + "'");

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
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        float dt = Gdx.graphics.getDeltaTime();

        // Update and render the world
        world.update(dt);

        // Update step count display if it has changed (for real-time simulation)
        if (world.getSim().getCurrentTime() != currentStep) {
            currentStep = world.getSim().getCurrentTime();
            topBar.updateStepCount(currentStep);
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        world.resize(width, height);

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        world.dispose();

        stage.dispose();
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }

    public void loadSimulation(String jsonPath) {
        Simulation sim = new Simulation(jsonPath);
        sim.setLogger(this.world.getLogger()); // Use the same logger
        this.world.setSimulation(sim);
    }

    public void saveSimulation(String jsonPath) {
        this.world.getSim().save(jsonPath);
    }

    // set verbosity
    public void setVerbosity(int verbosity) {
        this.world.getSim().setVerbosity(verbosity);
    }

    // make user request
    public void makeUserRequest(String itemName, String buildingName) {
        this.world.getSim().makeUserRequest(itemName, buildingName);
    }

    //get current step
    public int getCurrentStep() {
        return this.world.getSim().getCurrentTime();
    }

    //step simulation by n steps
    public void step(int n) {
        // Stop real-time simulation if user choose to step manually
        if (this.world.isRealTimeEnabled()) {
            stopRealTimeSimulation();
        }
        this.world.getSim().step(n);
    }

    //set policy
    public void setPolicy(String type, String policy, String buildingName) {
        this.world.getSim().setPolicy(type, policy, buildingName);
    }

    //finish simulation
    public void finish() {
        // Stop real-time simulation if user choose to finish
        if (this.world.isRealTimeEnabled()) {
            stopRealTimeSimulation();
        }
        this.world.getSim().finish();
    }

    /**
     * Starts real-time simulation.
     */
    public void startRealTimeSimulation() {
        this.world.getRealTime().start();
        this.world.enableRealTime();
    }

    /**
     * Pauses real-time simulation.
     */
    public void pauseRealTimeSimulation() {
        if (this.world.isRealTimeEnabled()) {
            this.world.getRealTime().pause();
        }
    }

    /**
     * Resumes real-time simulation from a paused state.
     */
    public void resumeRealTimeSimulation() {
        if (this.world.isRealTimeEnabled()) {
            this.world.getRealTime().resume();
        }
    }

    /**
     * Stops real-time simulation.
     */
    public void stopRealTimeSimulation() {
        this.world.getRealTime().stop();
        this.world.disableRealTime();
    }

    /**
     * Sets the speed of real-time simulation.
     *
     * @param stepsPerSecond steps per second
     */
    public void setRealTimeSpeed(float stepsPerSecond) {
        this.world.getRealTime().setSpeed(stepsPerSecond);
    }

    /**
     * Gets the current real-time simulation speed.
     *
     * @return steps per second
     */
    public float getRealTimeSpeed() {
        return this.world.getRealTime().getSpeed();
    }

    /**
     * Checks if real-time simulation is enabled.
     *
     * @return true if enabled
     */
    public boolean isRealTimeEnabled() {
        return this.world.isRealTimeEnabled();
    }

    /**
     * Checks if real-time simulation is paused.
     *
     * @return true if paused
     */
    public boolean isRealTimePaused() {
        return this.world.getRealTime().isPaused();
    }
}
