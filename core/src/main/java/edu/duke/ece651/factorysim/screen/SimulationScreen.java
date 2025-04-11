package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.ui.style.UIButtonStyle;
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

        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Initialize top bar
        topBar = new TopBar();

        // Initialize log panel
        logPanel = new LogPanel();

        // Initialize info panel
        infoPanel = new InfoPanel();

        // Initialize control panel
        controlPanel = new ControlPanel();

        // add all panels to root
        root.add(topBar).colspan(3).expandX().fillX().pad(10).row();
        root.add(logPanel).width(200).expandY().fillY().top().pad(10);
        root.add().expand().fill();  // map area
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
