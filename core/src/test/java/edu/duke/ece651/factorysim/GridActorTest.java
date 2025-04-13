package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GridActorTest {
    @Test
    public void test_getCols() {
        GridActor grid = createGridActor(5, 3, 16);
        assertEquals(5, grid.getCols());
    }

    @Test
    public void test_getRows() {
        GridActor grid = createGridActor(5, 3, 16);
        assertEquals(3, grid.getRows());
    }

    @Test
    public void test_getWidth() {
        GridActor grid = createGridActor(5, 3, 16);
        assertEquals(5 * 16, grid.getWidth());
    }

    @Test
    public void test_getHeight() {
        GridActor grid = createGridActor(5, 3, 16);
        assertEquals(3 * 16, grid.getHeight());
    }

    @Test
    public void test_getSelectTexture() {
        Texture selectTexture = mock(Texture.class);
        GridActor grid = createGridActor(5, 3, 16, selectTexture);
        assertSame(selectTexture, grid.getSelectTexture());
    }

    @Test
    public void test_setSelectTexture() {
        Texture texture1 = mock(Texture.class);
        Texture texture2 = mock(Texture.class);
        GridActor grid = createGridActor(5, 3, 16, texture1);
        grid.setSelectTexture(texture2);
        assertSame(texture2, grid.getSelectTexture());
    }

    @Test
    public void test_draw() {
        Texture cellTexture = mock(Texture.class);
        Texture selectTexture = mock(Texture.class);
        GridActor grid = new GridActor(3, 2, 16, cellTexture, selectTexture, 0f, 0f);
        SpriteBatch spriteBatch = mock(SpriteBatch.class);
        grid.draw(spriteBatch);
        verify(spriteBatch, times(6)).draw(eq(cellTexture), anyFloat(), anyFloat());
    }

    @Test
    public void test_drawSelectionBox() {
        Texture selectTexture = mock(Texture.class);
        GridActor grid = createGridActor(5, 5, 16, selectTexture);
        SpriteBatch spriteBatch = mock(SpriteBatch.class);
        grid.onMouseMoved(new Vector2(24f, 32f));
        grid.drawSelectionBox(spriteBatch);
        verify(spriteBatch).draw(eq(selectTexture), eq(16f), eq(32f));

        spriteBatch = mock(SpriteBatch.class);
        grid.onMouseMoved(new Vector2(100f, 32f));
        grid.drawSelectionBox(spriteBatch);
        verify(spriteBatch, never()).draw(eq(selectTexture), anyFloat(), anyFloat());

        spriteBatch = mock(SpriteBatch.class);
        grid.onMouseMoved(new Vector2(-17f, 32f));
        grid.drawSelectionBox(spriteBatch);
        verify(spriteBatch, never()).draw(eq(selectTexture), anyFloat(), anyFloat());

        spriteBatch = mock(SpriteBatch.class);
        grid.onMouseMoved(new Vector2(16f, -17f));
        grid.drawSelectionBox(spriteBatch);
        verify(spriteBatch, never()).draw(eq(selectTexture), anyFloat(), anyFloat());

        spriteBatch = mock(SpriteBatch.class);
        grid.onMouseMoved(new Vector2(16f, 100f));
        grid.drawSelectionBox(spriteBatch);
        verify(spriteBatch, never()).draw(eq(selectTexture), anyFloat(), anyFloat());
    }

    @Test
    public void test_resize() {
        GridActor grid = createGridActor(2, 2, 16);
        grid.resize(10, 8);
        assertEquals(10, grid.getCols());
        assertEquals(8, grid.getRows());
    }

    @Test
    public void test_onMouseMoved() {
        GridActor grid = createGridActor(5, 5, 16);
        Vector2 newPos = new Vector2(100f, 50f);
        grid.onMouseMoved(newPos);
    }

    private static GridActor createGridActor(int cols, int rows, int cellSize, Texture selectTexture) {
        Texture cellTexture = mock(Texture.class);
        return new GridActor(cols, rows, cellSize, cellTexture, selectTexture, 0f, 0f);
    }

    private static GridActor createGridActor(int cols, int rows, int cellSize) {
        return createGridActor(cols, rows, cellSize, mock(Texture.class));
    }
}
