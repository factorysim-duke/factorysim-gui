package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
public class FactoryGame extends Game {
    private Simulation sim;

    //constructor
    public FactoryGame() {
        super();
        this.sim = new Simulation("doors1.json");
    }

    public void loadSimulation(String jsonPath) {
        this.sim.load(jsonPath);
    }

    public int getCurrentStep() {
        return this.sim.getCurrentTime();
    }

    public void step(int n) {
        this.sim.step(n);
    }

    @Override
    public void create() {
        SimulationScreen simulationScreen = new SimulationScreen(this);
        this.setScreen(simulationScreen);
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
