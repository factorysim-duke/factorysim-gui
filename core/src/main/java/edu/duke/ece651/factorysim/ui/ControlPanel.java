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

        VisLabel controlLabel = new VisLabel("Control");
        controlLabel.setColor(Color.BLACK);

        finishButton = new VisTextButton("Finish", "red");
        finishButton.pad(5, 10, 5, 10);

        IntSpinnerModel spinnerModel = new IntSpinnerModel(1, 1, 100, 1);
        stepSpinner = new Spinner("", spinnerModel);
        stepSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentSteps = (int) spinnerModel.getValue();
            }
        });

        // Let the spinner handle the click event, not the button
        stepSpinner.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();  // stop the event from bubbling up to the button
                return super.touchDown(event, x, y, pointer, button);
            }
        });


        stepButton = new VisTextButton("", "blue");
        stepButton.setSize(100, 100); // adjust as needed

        Table overlay = new Table();
        VisLabel stepLabel = new VisLabel("Step");
        stepLabel.setColor(Color.WHITE); // make the text more visible on the blue button
        overlay.add(stepLabel).expandX().center().padBottom(5).row();
        overlay.add(stepSpinner).width(50).height(30).center();
        stepButton.clearChildren();
        stepButton.add(overlay).expand().fill();


        add(controlLabel).colspan(2).padBottom(5).row();
        add(stepButton).size(100, 100).pad(5);
        add(finishButton).pad(5).row();
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
