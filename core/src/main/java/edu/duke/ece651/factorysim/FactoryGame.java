package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
public class FactoryGame extends Game {
    private Simulation sim;

    //TODO: double check how to work with gameworld
    //constructor
    public FactoryGame() {
        super();
        this.sim = new Simulation("doors1.json");
    }

    //load simulation from json file
    public void loadSimulation(String jsonPath) {
        this.sim.load(jsonPath);
    }

    //get current step
    public int getCurrentStep() {
        return this.sim.getCurrentTime();
    }

    //step simulation by n steps
    public void step(int n) {
        this.sim.step(n);
    }

    //finish simulation
    public void finish() {
        this.sim.finish();
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
