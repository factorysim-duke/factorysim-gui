package edu.duke.ece651.factorysim.ui.style;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

public class UIButtonStyle {
    public static void registerCustomStyles() {
        Skin skin = VisUI.getSkin();

        // orange button
        VisTextButtonStyle orangeStyle = new VisTextButtonStyle(skin.get(VisTextButtonStyle.class));
        orangeStyle.fontColor = Color.BLACK;
        orangeStyle.up = skin.newDrawable("white", new Color(1.0f, 0.8f, 0.5f, 1f));  // 柔橘
        orangeStyle.down = skin.newDrawable("white", new Color(1.0f, 0.7f, 0.3f, 1f));
        orangeStyle.over = skin.newDrawable("white", new Color(1.0f, 0.85f, 0.55f, 1f));
        skin.add("orange", orangeStyle);

        // red button
        VisTextButtonStyle redStyle = new VisTextButtonStyle(skin.get(VisTextButtonStyle.class));
        redStyle.fontColor = Color.BLACK;
        redStyle.up = skin.newDrawable("white", new Color(1.0f, 0.6f, 0.6f, 1f));  // 柔红
        redStyle.down = skin.newDrawable("white", new Color(1.0f, 0.4f, 0.4f, 1f));
        redStyle.over = skin.newDrawable("white", new Color(1.0f, 0.5f, 0.5f, 1f));
        skin.add("red", redStyle);

        // blue button
        VisTextButtonStyle blueStyle = new VisTextButtonStyle(skin.get(VisTextButtonStyle.class));
        blueStyle.fontColor = Color.WHITE;
        blueStyle.up = skin.newDrawable("white", new Color(0.4f, 0.6f, 0.9f, 1f));
        blueStyle.down = skin.newDrawable("white", new Color(0.3f, 0.5f, 0.8f, 1f));
        blueStyle.over = skin.newDrawable("white", new Color(0.5f, 0.7f, 1f, 1f));
        skin.add("blue", blueStyle);
    }
}
