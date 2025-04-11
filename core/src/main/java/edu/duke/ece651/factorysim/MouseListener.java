package edu.duke.ece651.factorysim;

/**
 * Interface for a listener that responds to mouse movement events.
 */
public interface MouseListener {
    /**
     * Invoked when the mouse moves to a new position.
     *
     * @param mouseX the current x coordinate of the mouse in world space.
     * @param mouseY the current y coordinate of the mouse in world space.
     */
    void onMouseMoved(float mouseX, float mouseY);
}
