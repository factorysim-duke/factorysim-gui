package edu.duke.ece651.factorysim;

import com.badlogic.gdx.Game;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
import edu.duke.ece651.factorysim.util.PanelLogger;

public class FactoryGame extends Game {
    private Simulation sim;
    private PanelLogger logger;

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

    //save simulation to json file
    public void saveSimulation(String jsonPath) {
        this.sim.save(jsonPath);
    }

    //set logger
    public void setLogger(PanelLogger logger) {
        this.logger = logger;
        this.sim.setLogger(this.logger);
    }

    // set verbosity
    public void setVerbosity(int verbosity) {
        this.sim.setVerbosity(verbosity);
    }

    // make user request
    public void makeUserRequest(String itemName, String buildingName) {
        this.sim.makeUserRequest(itemName, buildingName);
    }

    //get current step
    public int getCurrentStep() {
        return this.sim.getCurrentTime();
    }

    //step simulation by n steps
    public void step(int n) {
        this.sim.step(n);
    }

    //set policy
    public void setPolicy(String type, String policy, String buildingName) {
        this.sim.setPolicy(type, policy, buildingName);
    }

    //finish simulation
    public void finish() {
        this.sim.finish();
    }

    @Override
    public void create() {
        SimulationScreen simulationScreen = new SimulationScreen(this);
        this.setScreen(simulationScreen);
        // TODO: Hardcode building info request, remove later
        Building building = this.sim.getWorld().getBuildingFromName("D");
        simulationScreen.showBuildingInfo(building);
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
