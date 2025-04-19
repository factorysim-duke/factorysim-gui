package edu.duke.ece651.factorysim;

import com.badlogic.gdx.math.Vector2;

public class Vec2Utils {
    public static void moveTowards(Vector2 current, Vector2 target, float maxDelta) {
        float dx = target.x - current.x;
        float dy = target.y - current.y;
        float sqrDistance = dx * dx + dy * dy;
        if (sqrDistance == 0 || (maxDelta >= 0 && sqrDistance <= maxDelta * maxDelta)) {
            current.set(target);
            return;
        }
        float distance = (float)Math.sqrt(sqrDistance);
        current.set(current.x + dx / distance * maxDelta, current.y + dy / distance * maxDelta);
    }
}
