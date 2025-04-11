package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Array;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;

import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.Simulation;
import edu.duke.ece651.factorysim.ui.style.UIButtonStyle;
import edu.duke.ece651.factorysim.ui.style.UISelectBoxStyle;
import edu.duke.ece651.factorysim.ui.TopBar;
import edu.duke.ece651.factorysim.ui.LogPanel;
import edu.duke.ece651.factorysim.ui.InfoPanel;
import edu.duke.ece651.factorysim.ui.ControlPanel;

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

        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        UIButtonStyle.registerCustomStyles();
        UISelectBoxStyle.registerCustomStyles();

        FileChooser.setFavoritesPrefsName("edu.duke.ece651.factorysim.filechooser");
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);

        FileTypeFilter typeFilter = new FileTypeFilter(true);
        typeFilter.addRule("Simulation files (*.json)", "json");
        fileChooser.setFileTypeFilter(typeFilter);

        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setFileTypeFilter(typeFilter);


        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    String jsonPath = files.first().file().getAbsolutePath();
                    try {
                        game.loadSimulation(jsonPath);
                        System.out.println("Simulation loaded from: " + jsonPath);
                    } catch (Exception e) {
                        System.err.println("Failed to load simulation: " + e.getMessage());
                    }
                }
            }
        });

        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Initialize top bar
        topBar = new TopBar();
        topBar.getLoadButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(fileChooser.fadeIn());
            }
        });

        // Initialize other UI panels
        logPanel = new LogPanel();
        infoPanel = new InfoPanel();
        controlPanel = new ControlPanel();

        // Add panels to root
        root.add(topBar).colspan(3).expandX().fillX().pad(10).row();
        root.add(logPanel).width(200).expandY().fillY().top().pad(10);
        root.add().expand().fill();  // center map
        root.add(infoPanel).width(200).top().pad(10).row();
        root.add().colspan(2).expandX().fillX();
        root.add(controlPanel).right().pad(10);
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
