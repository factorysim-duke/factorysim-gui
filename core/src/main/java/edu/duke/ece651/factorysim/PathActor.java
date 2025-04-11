package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.function.Function;

public class PathActor extends Actor2D {
    private final Path path;
    private final TileMap tileMap;

    private final Animator<TextureRegion> pathAnimator;
    private final TextureRegion crossTexture;

    private final Function<Coordinate, Vector2> coordinateToWorld;

    public PathActor(Path path, TileMap tileMap,
                     Animator<TextureRegion> pathAnimator, Texture crossTexture,
                     Function<Coordinate, Vector2> coordinateToWorld) {
        super(0f, 0f);
        this.path = path;
        this.tileMap = tileMap;
        this.pathAnimator = pathAnimator;
        this.crossTexture = new TextureRegion(crossTexture);
        this.coordinateToWorld = coordinateToWorld;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        int[] flowDirs = new int[2];

        List<Coordinate> steps = path.getSteps();

        // Draw paths
        for (Coordinate c : steps) {
            if (!canDraw(c)) {
                continue;
            }

            int[] flows = tileMap.getFlows(c);
            int num = getFlowDirs(flows, flowDirs);
            if (num == 2 && isLinear(flowDirs[0], flowDirs[1])) {
                drawPath(spriteBatch, c, flows, flowDirs);
            }
        }

        // Draw crosses
        for (int i = 0; i < steps.size(); i++) {
            Coordinate c = steps.get(i);
            if (!canDraw(c)) {
                continue;
            }

            // Always draw near the source and destination buildings
            if (i == 1 || i == steps.size() - 2) {
                drawCross(spriteBatch, c);
                continue;
            }

            int num = getFlowDirs(tileMap.getFlows(c), flowDirs);
            if (num != 2 || !isLinear(flowDirs[0], flowDirs[1])) {
                drawCross(spriteBatch, c);
            }
        }
    }

    private static final int UP    = 0;
    private static final int RIGHT = 1;
    private static final int DOWN  = 2;
    private static final int LEFT  = 3;

    private boolean canDraw(Coordinate c) {
        return tileMap.isInsideMap(c) && tileMap.getTileType(c) == TileType.PATH;
    }

    private static int getFlowDirs(int[] flows, int[] out) {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            if (flows[i] != 0) {
                if (num < 2) {
                    out[num] = i;
                }
                num++;
            }
        }
        return num;
    }

    private static boolean isLinear(int d1, int d2) {
        return (d1 == UP && d2 == DOWN) || (d1 == DOWN && d2 == UP) ||
               (d1 == LEFT && d2 == RIGHT) || (d1 == RIGHT && d2 == LEFT);
    }

    private static float getRotationFromDirection(int dir) {
        return switch (dir) {
            case UP    -> 90f;
            case RIGHT -> 180f;
            case DOWN  -> 270f;
            default    -> 0f;
        };
    }

    private void drawPath(SpriteBatch spriteBatch, Coordinate c, int[] flows, int[] dirs) {
        int out = (flows[dirs[0]] > 0) ? dirs[0] : dirs[1];
        float rotation = getRotationFromDirection(out);

        TextureRegion texture = pathAnimator.getCurrentKeyFrame();
        int width = texture.getRegionWidth();
        int height = texture.getRegionHeight();
        Vector2 pos = coordinateToWorld.apply(c);

        spriteBatch.draw(texture, pos.x, pos.y, width / 2f, height / 2f,
                width, height, 1f, 1f, rotation);
    }

    private void drawCross(SpriteBatch spriteBatch, Coordinate c) {
        Vector2 pos = coordinateToWorld.apply(c);
        spriteBatch.draw(crossTexture, pos.x, pos.y);
    }
}
