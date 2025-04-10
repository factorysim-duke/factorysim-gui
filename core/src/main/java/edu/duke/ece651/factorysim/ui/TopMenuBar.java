package edu.duke.ece651.factorysim.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import edu.duke.ece651.factorysim.FactoryGame;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.Gdx;

public class TopMenuBar extends Table {
    public TopMenuBar(FactoryGame game) {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        TextButton saveButton = new TextButton("Save", skin);
        TextButton loadButton = new TextButton("Load", skin);

        add(saveButton);
        add(loadButton);

        row();

    }
}
