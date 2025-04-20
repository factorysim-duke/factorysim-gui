package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class BuildingActorTest {
    private Building mockBuilding;
    private Animation<TextureRegion> mockAnimation;
    private TextureRegion mockFrame;
    private Texture mockTexture;
    private SpriteBatch mockBatch;

    @BeforeEach
    public void setup() {
        mockBuilding = mock(Building.class);
        mockAnimation = mock(Animation.class);
        mockFrame = mock(TextureRegion.class);
        mockTexture = mock(Texture.class);
        mockBatch = mock(SpriteBatch.class);

        Graphics mockGraphics = mock(Graphics.class);
        Gdx.graphics = mockGraphics;
        when(mockGraphics.getDeltaTime()).thenReturn(0.01f);
    }

    @Test
    public void test_constructor_getBuilding() {
        BuildingActor actor = new BuildingActor(mockBuilding, mockAnimation, mockTexture, 1f, 2f);
        assertSame(mockBuilding, actor.getBuilding());
        assertEquals(1f, actor.position.x);
        assertEquals(2f, actor.position.y);
    }

    @Test
    public void test_draw() {
        when(mockAnimation.getKeyFrame(anyFloat(), eq(true))).thenReturn(mockFrame);

        BuildingActor actor = new BuildingActor(mockBuilding, mockAnimation, mockTexture, 5f, 10f);
        actor.draw(mockBatch);

        verify(mockAnimation).getKeyFrame(anyFloat(), eq(true));
        verify(mockBatch).draw(mockFrame, 5f, 10f);
    }
}
