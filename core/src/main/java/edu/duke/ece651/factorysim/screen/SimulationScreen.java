package edu.duke.ece651.factorysim.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import edu.duke.ece651.factorysim.FactoryGame;
import com.badlogic.gdx.graphics.Color;

public class SimulationScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private FactoryGame game;
    private Label stepCountLabel;
    private int currentStep = 0;

    public SimulationScreen(FactoryGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // header table
        Table headerTable = new Table();
        Label titleLabel = new Label("Factorysim", skin);
        titleLabel.setFontScale(1.5f);
        stepCountLabel = new Label("Current Step: " + currentStep, skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton loadButton = new TextButton("Load", skin);

        headerTable.add(titleLabel).left();
        headerTable.add(stepCountLabel).center().expandX();
        Table rightButtons = new Table();
        rightButtons.add(saveButton).padRight(20);
        rightButtons.add(loadButton);
        headerTable.add(rightButtons).right();

        // log panel
        Table logPanel = new Table();
        logPanel.top();
        Label logsLabel = new Label("Logs", skin);
        SelectBox<String> verbosityBox = new SelectBox<>(skin);
        verbosityBox.setItems("0", "1", "2");
        TextArea logArea = new TextArea("", skin);
        logArea.setDisabled(true);

        logPanel.add(logsLabel).left().row();
        logPanel.add(new Label("verbose:", skin)).left();
        logPanel.add(verbosityBox).left().row();
        logPanel.add(logArea).expand().fill().colspan(2);

        // right info panel
        Table rightPanel = new Table();
        rightPanel.top();

        Label buildingLabel = new Label("Building 'D'", skin);
        Label policyLabel = new Label("Policy:", skin);
        SelectBox<String> policyBox = new SelectBox<>(skin);
        policyBox.setItems("FIFO");

        Label outputsLabel = new Label("Outputs: door", skin);
        Label sourcesLabel = new Label("Sources:", skin);
        Label queueLabel = new Label("Request Queue:", skin);
        TextButton newRequestButton = new TextButton("New Request", skin);

        rightPanel.add(buildingLabel).left().row();
        rightPanel.add(policyLabel).left();
        rightPanel.add(policyBox).left().row();
        rightPanel.add(outputsLabel).left().row();
        rightPanel.add(sourcesLabel).left().row();
        rightPanel.add(queueLabel).left().row();
        rightPanel.add(newRequestButton).fillX().row();

        // control button area
        Table controlPanel = new Table();
        TextButton stepButton = new TextButton("Step", skin);
        TextButton finishButton = new TextButton("Finish", skin);
        controlPanel.add(stepButton).padRight(20);
        controlPanel.add(finishButton);

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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);

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
        skin.dispose();
    }
}
