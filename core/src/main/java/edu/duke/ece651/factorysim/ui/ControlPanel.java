package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.spinner.*;

public class ControlPanel extends VisTable {
    private VisTextButton stepButton;
    private VisTextButton finishButton;
    private Spinner stepSpinner;
    private int currentSteps = 1;

    public ControlPanel() {
        super();
        init();
    }

    private void init() {
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));

        // Control label
        VisLabel controlLabel = new VisLabel("Control");
        controlLabel.setColor(Color.BLACK);

        // Create circular step button container
        VisTable stepContainer = new VisTable();

        // Step spinner
        IntSpinnerModel spinnerModel = new IntSpinnerModel(1, 1, 100, 1);
        stepSpinner = new Spinner("", spinnerModel);

        // Add click listener to spinner to stop event propagation
        stepSpinner.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        // Add value change listener to spinner
        stepSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentSteps = (int) spinnerModel.getValue();
            }
        });

        // Finish button
        finishButton = new VisTextButton("Finish", "red");
        finishButton.pad(5, 10, 5, 10);

        // Layout the step container
        VisLabel stepLabel = new VisLabel("Step");
        stepContainer.center();  // Center the container itself
        stepContainer.add(stepLabel).center().padBottom(5).row();
        stepContainer.add(stepSpinner).center().width(40).row();

        // Create the actual step button that will receive click events
        stepButton = new VisTextButton("", "blue");

        // 用Table包裹stepContainer，让它在stepButton中垂直居中
        Table wrapper = new Table();
        wrapper.add(stepContainer).center();
        wrapper.center();

        stepButton.clearChildren(); // 清除原有内容
        stepButton.add(wrapper).expand().fill().center();


        // Add all to panel
        add(controlLabel).colspan(2).padBottom(5).row();
        add(stepButton).size(100, 100).padRight(20).pad(5);
        add(finishButton).padLeft(10).pad(5);
    }

    public VisTextButton getStepButton() {
        return stepButton;
    }

    public VisTextButton getFinishButton() {
        return finishButton;
    }

    public int getStepCount() {
        return currentSteps;
    }
}
