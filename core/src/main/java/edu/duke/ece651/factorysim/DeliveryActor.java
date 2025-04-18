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
    }

    public void draw(SpriteBatch spriteBatch) {
        Coordinate c = delivery.getCurrentCoordinate();
        Vector2 pos = coordinateToWorld.apply(c);
        spriteBatch.draw(texture, pos.x, pos.y);
    }
}
