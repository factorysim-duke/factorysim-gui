package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.function.Function;

public class DeliveryActor extends Actor2D {

    private final Delivery delivery;

    private final Texture texture;

    private final Function<Coordinate, Vector2> coordinateToWorld;

    /**
     * Get the underlying `Delivery` instance of the delivery actor.
     *
     * @return the `Delivery` instance of the actor.
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * Constructs a `DeliveryActor` instance with a given absolute position.
     *
     * @param delivery is the `Delivery` instance the actor used to track delivery status.
     * @param texture is the texture of the delivery actor.
     * @param coordinateToWorld is the function to convert a coordinate to a position in the world.
     */
    public DeliveryActor(Delivery delivery, Texture texture, Function<Coordinate, Vector2> coordinateToWorld) {
        super(0f, 0f);

        this.delivery = delivery;
        this.texture = texture;
        this.coordinateToWorld = coordinateToWorld;

        this.position.set(coordinateToWorld.apply(delivery.getCurrentCoordinate()));
    }

    private final Vector2 startPos = new Vector2();
    private final Vector2 targetPos = new Vector2();
    private float progress = 1f;
    private float duration = 0f;

    public void update(float dt, float stepsPerSecond) {
        Vector2 newTarget = coordinateToWorld.apply(delivery.getCurrentCoordinate());

        if (!newTarget.epsilonEquals(targetPos, 0.01f)) {
            startPos.set(position);
            targetPos.set(newTarget);
            progress = 0f;
            duration = 1f / stepsPerSecond;
        }

        progress = Math.min(progress + dt / duration, 1f);
        position.set(startPos).lerp(targetPos, progress);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, position.x, position.y);
    }

    public boolean hasArrived() {
        return delivery.isArrive() && position.epsilonEquals(targetPos);
    }
}
