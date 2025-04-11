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

public class SimulationScreen implements Screen {
    private Stage stage;
    private FactoryGame game;
    private VisLabel stepCountLabel;
    private int currentStep = 0;

    public SimulationScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        if (!VisUI.isLoaded()) {
            VisUI.load(); // 加载 VisUI 默认皮肤
        }

        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // header table
        VisTable headerTable = new VisTable();
        VisLabel titleLabel = new VisLabel("Factorysim");
        titleLabel.setFontScale(2.5f);

        stepCountLabel = new VisLabel("Current Step: " + currentStep);
        VisTextButtonStyle redStyle = new VisTextButtonStyle(
        VisUI.getSkin().get(VisTextButtonStyle.class));
        redStyle.up = VisUI.getSkin().newDrawable("white", Color.ORANGE);
        redStyle.down = VisUI.getSkin().newDrawable("white", new Color(1.0f, 0.5f, 0.1f, 1));
        redStyle.over = VisUI.getSkin().newDrawable("white", new Color(1.0f, 0.6f, 0.2f, 1));

        VisUI.getSkin().add("red", redStyle);


        VisTextButton saveButton = new VisTextButton("Save", "red");
        VisTextButton loadButton = new VisTextButton("Load");

        saveButton.pad(5, 10, 5, 10);
        loadButton.pad(5, 10, 5, 10);

        headerTable.add(titleLabel).left().padLeft(20);
        headerTable.add(stepCountLabel).center().expandX();
        VisTable rightButtons = new VisTable();
        rightButtons.add(saveButton).padRight(20);
        rightButtons.add(loadButton);
        headerTable.add(rightButtons).right();

        // log panel
        VisTable logPanel = new VisTable();
        logPanel.setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        logPanel.top();
        VisLabel logsLabel = new VisLabel("Logs");

        VisSelectBox<String> verbosityBox = new VisSelectBox<>();
        verbosityBox.setItems("0", "1", "2");

        VisTextArea logArea = new VisTextArea("");
        logArea.setDisabled(true);

        VisTable verboseTable = new VisTable();
        VisLabel verboseLabel = new VisLabel("verbose:");
        verboseTable.add(verboseLabel).left();
        verboseTable.add(verbosityBox).left().padLeft(5).pad(5, 10, 5, 10);

        logPanel.add(logsLabel).left().padLeft(10).padTop(10).row();
        logPanel.add(verboseTable).left().padLeft(10).padTop(5).row();
        logPanel.add(logArea).expand().fill().pad(10);

        // right info panel
        VisTable rightPanel = new VisTable();
        rightPanel.setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        rightPanel.top();

        VisLabel buildingLabel = new VisLabel("Building 'D'");
        VisLabel policyLabel = new VisLabel("Policy:");
        VisSelectBox<String> policyBox = new VisSelectBox<>();
        policyBox.setItems("FIFO");

        VisLabel outputsLabel = new VisLabel("Outputs: door");
        VisLabel sourcesLabel = new VisLabel("Sources:");
        VisLabel queueLabel = new VisLabel("Request Queue:");
        VisTextButton newRequestButton = new VisTextButton("New Request");

        rightPanel.add(buildingLabel).left().padLeft(10).padTop(10).row();
        rightPanel.add(policyLabel).left().padLeft(10).padTop(5);
        rightPanel.add(policyBox).left().padLeft(5).pad(5, 10, 5, 10).row();
        rightPanel.add(outputsLabel).left().padLeft(10).padTop(5).row();
        rightPanel.add(sourcesLabel).left().padLeft(10).padTop(5).row();
        rightPanel.add(queueLabel).left().padLeft(10).padTop(5).row();
        rightPanel.add(newRequestButton).fillX().pad(10).row();

        // control button area
        VisTable controlPanel = new VisTable();
        controlPanel.setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        VisTextButton stepButton = new VisTextButton("Step", "blue");
        VisTextButton finishButton = new VisTextButton("Finish", "blue");
        controlPanel.add(stepButton).padRight(20).padLeft(10).padTop(5).padBottom(5);
        controlPanel.add(finishButton).padRight(10).padTop(5).padBottom(5);

        // add all panels to root
        root.add(headerTable).colspan(3).expandX().fillX().pad(10).row();
        root.add(logPanel).width(200).expandY().fillY().top().pad(10);
        root.add().expand().fill();  // map area
        root.add(rightPanel).width(200).top().pad(10).row();
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
