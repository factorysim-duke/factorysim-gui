package edu.duke.ece651.factorysim.ui.style;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.kotcrab.vis.ui.VisUI;

public class UISelectBoxStyle {
    public static void registerCustomStyles() {
        Skin skin = VisUI.getSkin();

        SelectBoxStyle baseStyle = skin.get(SelectBoxStyle.class);
        SelectBoxStyle blueStyle = new SelectBoxStyle(baseStyle);


        // list style
        ListStyle listStyle = new ListStyle(baseStyle.listStyle);
        listStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        listStyle.selection = skin.newDrawable("white", new Color(0.3f, 0.5f, 0.8f, 1f)); // selected color
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.fontColorUnselected = Color.WHITE;
        blueStyle.listStyle = listStyle;

        // scroll style
        ScrollPaneStyle scrollStyle = new ScrollPaneStyle(baseStyle.scrollStyle);
        scrollStyle.background = skin.newDrawable("white", new Color(0.9f, 0.95f, 1f, 1f));
        blueStyle.scrollStyle = scrollStyle;

        skin.add("blue", blueStyle);
    }
}
