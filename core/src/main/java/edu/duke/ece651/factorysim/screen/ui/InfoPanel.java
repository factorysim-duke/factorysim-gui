package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * A generic panel base class.
 */
public class InfoPanel extends VisTable {

    public InfoPanel() {
        super();
        setVisible(true);
        setBackground(VisUI.getSkin().newDrawable("white", new Color(0.95f, 0.95f, 0.95f, 0.9f)));
        pad(15);
        top().left();
    }

    /**
     * Show specific building information.
     */
    public void updateData(Object data) {

    }

    public VisTextButton getNewRequestButton() {
        return null;
    }
}
