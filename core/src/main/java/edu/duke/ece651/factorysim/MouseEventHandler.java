package edu.duke.ece651.factorysim;

import java.util.*;

/**
 * Represents an event handler that dispatches mouse movement events to subscribed listeners.
 */
public class MouseEventHandler {
    private final List<MouseListener> listeners = new ArrayList<>();

    private boolean hasValue = false;
    private float prevMouseX;
    private float prevMouseY;

    /**
     * Subscribes a listener to receive mouse movement events.
     *
     * @param listener is the listener to subscribe.
     */
    public void subscribe(MouseListener listener) {
        listeners.add(listener);
    }

    /**
     * Unsubscribes a listener from receiving mouse movement events.
     *
     * @param listener is the listener to unsubscribe.
     */
    public void unsubscribe(MouseListener listener) {
        listeners.remove(listener);
    }

    /**
     * Updates the current mouse position. If the position has changed, all subscribed listeners are notified.
     *
     * @param mouseX is the current x coordinate of the mouse in world space.
     * @param mouseY is the current y coordinate of the mouse in world space.
     */
    public void update(float mouseX, float mouseY) {
        if (prevMouseX == mouseX && prevMouseY == mouseY && hasValue) {
            return;
        }
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        hasValue = true;

        for (MouseListener listener : listeners) {
            listener.onMouseMoved(mouseX, mouseY);
        }
    }
}
