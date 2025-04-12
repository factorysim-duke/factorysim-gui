package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.g2d.Animation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnimatorTest {
    @Test
    public void test_constructor() {
        Animation<Integer> mockAnimation = mock(Animation.class);
        Animator<Integer> animator = new Animator<>(mockAnimation, true);
        assertEquals(0f, animator.getCurrentTime(), 0.0001f);
    }

    @Test
    public void test_step() {
        Animation<Integer> mockAnimation = mock(Animation.class);
        Animator<Integer> animator = new Animator<>(mockAnimation, false);
        animator.step(0.5f);
        animator.step(0.25f);
        assertEquals(0.75f, animator.getCurrentTime(), 0.0001f);
    }

    @Test
    public void test_getCurrentKeyFrame() {
        Animation<Integer> mockAnimation = mock(Animation.class);
        when(mockAnimation.getKeyFrame(1.0f, true)).thenReturn(123);

        Animator<Integer> animator = new Animator<>(mockAnimation, true);
        animator.step(1f);
        int frame = animator.getCurrentKeyFrame();

        assertEquals(123, frame);
        verify(mockAnimation).getKeyFrame(1f, true);
    }
}
