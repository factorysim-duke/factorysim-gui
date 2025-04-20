package edu.duke.ece651.factorysim;

/**
 * Handles real-time simulation by accumulating delta time and stepping the simulation.
 */
public class RealTimeSimulation {
    private final Simulation simulation;

    // Timing settings
    private float stepsPerSecond = 5f; // Default: 5 steps per second
    private float accumulatedTime = 0f;

    // State
    private boolean running = false;
    private boolean paused = false;

    /**
     * Creates a real-time simulation handler.
     *
     * @param simulation the simulation to handle
     */
    public RealTimeSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Updates the simulation based on the provided delta time.
     *
     * @param deltaTime time since the last frame in seconds
     * @return the number of steps executed this update
     */
    public int update(float deltaTime) {
        if (!running || paused) {
            return 0;
        }

        // Calculate how much time must pass to execute one step
        float timePerStep = 1f / stepsPerSecond;

        // Add the current frame's time to our accumulated time
        accumulatedTime += deltaTime;

        // Calculate how many steps to run this frame
        int stepsToRun = 0;

        while (accumulatedTime >= timePerStep) {
            simulation.step(1);
            accumulatedTime -= timePerStep;
            stepsToRun++;
        }

        return stepsToRun;
    }

    /**
     * Starts real-time simulation.
     */
    public void start() {
        running = true;
        paused = false;
        accumulatedTime = 0f;
    }

    /**
     * Pauses real-time simulation.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Resumes real-time simulation from a paused state.
     */
    public void resume() {
        if (running) {
            paused = false;
            // Reset accumulated time to avoid executing too many steps at once
            // accumulatedTime = 0f;
        }
    }

    /**
     * Stops real-time simulation.
     */
    public void stop() {
        running = false;
        paused = false;
        accumulatedTime = 0f;
    }

    /**
     * Sets the simulation speed in steps per second.
     *
     * @param stepsPerSecond steps per second (min 0.1, max 100)
     */
    public void setSpeed(float stepsPerSecond) {
        this.stepsPerSecond = Math.max(0.1f, Math.min(100f, stepsPerSecond));
        // Reset accumulated time when changing speed to prevent sudden bursts
        this.accumulatedTime = 0f;
    }

    /**
     * Gets the current simulation speed in steps per second.
     *
     * @return steps per second
     */
    public float getSpeed() {
        return stepsPerSecond;
    }

    /**
     * Checks if the simulation is currently running.
     *
     * @return true if running, otherwise false
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if the simulation is paused.
     *
     * @return true if paused, otherwise false
     */
    public boolean isPaused() {
        return paused;
    }
}
