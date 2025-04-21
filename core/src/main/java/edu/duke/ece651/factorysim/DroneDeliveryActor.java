package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

import java.util.function.Function;

public class DroneDeliveryActor extends Actor2D {

    private final DroneDelivery droneDelivery;

    private final Animator<TextureRegion> animator;

    private final Function<Coordinate, Vector2> coordinateToWorld;

    private final Vector2 target;

    public DroneDeliveryActor(DroneDelivery droneDelivery, Animation<TextureRegion> animation,
                              Function<Coordinate, Vector2> coordinateToWorld) {
        super(0f, 0f);
        this.droneDelivery = droneDelivery;
        this.animator = new Animator<>(animation, true);
        this.coordinateToWorld = coordinateToWorld;

        this.position.set(coordinateToWorld.apply(droneDelivery.getCurrentCoordinate()));
        this.target = coordinateToWorld.apply(droneDelivery.destination.getLocation());
    }

    public void update(float dt, float stepsPerSecond) {
        // Step animator
        animator.step(dt);

        // Movement
        if (droneDelivery.deliveryTime <= 0) {
            return;
        }
        float speed = dt * stepsPerSecond / droneDelivery.deliveryTime;
        position.mulAdd(new Vector2(target).sub(position).nor(), target.dst(position) * speed);
    }

    public void step() {
        this.position.set(coordinateToWorld.apply(droneDelivery.getCurrentCoordinate()));
    }

    public void render(SpriteBatch spriteBatch) {
        // Calculate rotation
        float dx = target.x - this.position.x;
        float dy = target.y - this.position.y;
        float rot = (float)Math.toDegrees((float)Math.atan2(dy, dx));

        // Draw
        TextureRegion frame = animator.getCurrentKeyFrame();
        float width = frame.getRegionWidth();
        float height = frame.getRegionHeight();
        float originX = width / 2f;
        float originY = height / 2f;
        spriteBatch.draw(
            frame,
            position.x - originX, position.y - originY,
            originX, originY,
            width, height,
            1f, 1f,
            rot
        );
    }
}
