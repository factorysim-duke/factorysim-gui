package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;

public class FactoryGame extends Game {
    private Simulation sim;

    public void setSimulation(Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void create() {
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (this.getScreen() != null) {
            this.getScreen().dispose();
        }
    }
}
