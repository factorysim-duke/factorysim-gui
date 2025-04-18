package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class PathActorTest {
    @Test
    public void test_draw() {
        // Mock path
        Coordinate c1 = new Coordinate(5, 5);
        Coordinate c2 = new Coordinate(0, 0);
        Coordinate c3 = new Coordinate(1, 0);
        Coordinate c4 = new Coordinate(1, 1);
        Coordinate c5 = new Coordinate(1, 2);
        List<Coordinate> steps = List.of(c1, c2, c3, c4, c5);
        Path path = mock(Path.class);
        when(path.getSteps()).thenReturn(steps);

        // Create tile map
        TileMap tileMap = new TileMap(3, 3);
        tileMap.setTileType(c3, TileType.PATH);
        tileMap.setTileType(c4, TileType.PATH);
        tileMap.setTileType(c5, TileType.PATH);
        tileMap.setFlow(c3, 0, 1);
        tileMap.setFlow(c3, 2, -1);
        tileMap.setFlow(c4, 0, 1);
        tileMap.setFlow(c4, 1, -1);
        tileMap.setFlow(c5, 0, 1);
        tileMap.setFlow(c5, 2, -1);

        // Mock animator and textures
        TextureRegion texture = mock(TextureRegion.class);
        when(texture.getRegionWidth()).thenReturn(16);
        when(texture.getRegionHeight()).thenReturn(16);
        Animator<TextureRegion> animator = mock(Animator.class);
        when(animator.getCurrentKeyFrame()).thenReturn(texture);
        Texture cross = mock(Texture.class);

        // Mock sprite batch
        SpriteBatch spriteBatch = mock(SpriteBatch.class);

        PathActor actor = new PathActor(path, tileMap, animator, cross,
            c -> new Vector2(c.getX() * 16f, c.getY() * 16f));
        actor.drawPaths(spriteBatch);
        actor.drawCrosses(spriteBatch);
    }

    @Test
    public void test_canDraw() {
        TileMap tileMap = new TileMap(2, 2);
        Coordinate c1 = new Coordinate(1, 1);
        Coordinate c2 = new Coordinate(3, 3);
        tileMap.setTileType(c1, TileType.PATH);

        Path path = mock(Path.class);
        Texture texture = mock(Texture.class);
        Animator<TextureRegion> animator = mock(Animator.class);
        PathActor actor = new PathActor(path, tileMap, animator, texture, (c) -> new Vector2(0, 0));

        assertTrue(actor.canDraw(c1));
        assertFalse(actor.canDraw(c2));
        tileMap.setTileType(c1, TileType.BUILDING);
        assertFalse(actor.canDraw(c1));
    }

    @Test
    public void test_getFlowDirs() {
        int[] flows = new int[] { 1, 0, -1, 0 };
        int[] out = new int[2];
        int count = PathActor.getFlowDirs(flows, out);
        assertEquals(2, count);
        assertEquals(0, out[0]);
        assertEquals(2, out[1]);
    }

    @Test
    public void test_isLinear() {
        assertTrue(PathActor.isLinear(0, 2)); // UP & DOWN
        assertTrue(PathActor.isLinear(3, 1)); // LEFT & RIGHT
        assertFalse(PathActor.isLinear(0, 1)); // UP & RIGHT
        assertFalse(PathActor.isLinear(2, 3)); // DOWN & LEFT
    }


    @Test
    public void test_getRotationFromDirection() {
        assertEquals(90f, PathActor.getRotationFromDirection(0));
        assertEquals(180f, PathActor.getRotationFromDirection(1));
        assertEquals(270f, PathActor.getRotationFromDirection(2));
        assertEquals(0f, PathActor.getRotationFromDirection(3));
        assertEquals(0f, PathActor.getRotationFromDirection(999));
    }
}
