package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Actor2DTest {
    private static class TestActor extends Actor2D {
        public TestActor(float x, float y) {
            super(x, y);
        }
    }

    @Test
    public void test_position() {
        Actor2D actor = new TestActor(5f, 10f);
        assertEquals(5f, actor.position.x, 0.0001f);
        assertEquals(10f, actor.position.y, 0.0001f);

        actor.position.x += 1f;
        assertEquals(6f, actor.position.x, 0.0001f);

        actor.position.y -= 2f;
        assertEquals(8f, actor.position.y, 0.0001f);
    }
}
