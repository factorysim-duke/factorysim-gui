package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Represents a simple timer-based animation controller for libGDX animations.
 *
 * @param <T> is the frame type.
 */
public class Animator<T> {
    private final Animation<T> animation;

    private final boolean looping;

    private float timer;

    public float getCurrentTime() { return timer; }
    public void setCurrentTime(float time) { this.timer = time; }

    /**
     * Constructs an animator with the given animation and loop setting.
     *
     * @param animation is the animation to play.
     * @param looping whether the animation should loop.
     */
    public Animator(Animation<T> animation, boolean looping) {
        this.animation = animation;
        this.looping = looping;
        this.timer = 0f;
    }

    /**
     * Advances the animation timer by the given delta time.
     *
     * @param dt time in seconds since last update.
     */
    public void step(float dt) {
        timer += dt;
    }

    /**
     * Gets the current frame based on the internal timer.
     *
     * @return the current animation frame.
     */
    public T getCurrentKeyFrame() {
        return animation.getKeyFrame(timer, looping);
    }

    /**
     * Gets the current frame's index based on the internal timer.
     *
     * @return the current animation frame's index.
     */
    public int getCurrentKeyFrameIndex() {
        return animation.getKeyFrameIndex(timer);
    }
}
