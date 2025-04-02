package edu.duke.ece651.factorysim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashMap;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FactoryGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        String jsonPath = "doors1.json";
        Logger logger = new StreamLogger(System.out);
        Simulation sim = new Simulation(jsonPath, 1, logger);

        Item iron = new Item("iron");
        Recipe miningRecipe = new Recipe(iron, new HashMap<>(), 1);

        MineBuilding mine = new MineBuilding(miningRecipe, "ironMine", sim);
        System.out.println(mine.getName());
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
