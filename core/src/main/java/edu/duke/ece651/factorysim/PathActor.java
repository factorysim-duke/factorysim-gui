package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PathActor extends Actor2D {
    private final TileMap tileMap;

    private final Animator<TextureRegion> pathAnimator;
    private final TextureRegion crossTexture;

    private final Function<Coordinate, Vector2> coordinateToWorld;

    private final List<Coordinate> paths;
    private final List<Coordinate> crosses;

    public PathActor(Path path, TileMap tileMap,
                     Animator<TextureRegion> pathAnimator, Texture crossTexture,
                     Function<Coordinate, Vector2> coordinateToWorld) {
        super(0f, 0f);
        this.tileMap = tileMap;
        this.pathAnimator = pathAnimator;
        this.crossTexture = new TextureRegion(crossTexture);
        this.coordinateToWorld = coordinateToWorld;

        // Split paths and crosses
        this.paths = new ArrayList<>();
        this.crosses = new ArrayList<>();
        int[] flowDirs = new int[2];
        List<Coordinate> steps = path.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            Coordinate c = steps.get(i);

            // Ignore coordinates that can't be drawn
            if (!canDraw(c)) {
                continue;
            }

            // Get the flow of the path
            int[] flows = tileMap.getFlows(c);
            int num = getFlowDirs(flows, flowDirs);

            // Paths near the source and destination buildings are always crosses
            if (i == 1 || i == steps.size() - 2) {
                crosses.add(c);
                continue;
            }

            // Paths with two flows and is linear are directional paths
            if (num == 2 && isLinear(flowDirs[0], flowDirs[1])) {
                this.paths.add(c);
                continue;
            }

            // Default as a cross
            crosses.add(c);
        }

        // Sort crosses by y (lower y = being drawn later)
        crosses.sort((a, b) -> {
            float ay = coordinateToWorld.apply(a).y;
            float by = coordinateToWorld.apply(b).y;
            return Float.compare(by, ay);
        });
    }

    public void drawPaths(SpriteBatch spriteBatch) {
        int[] flowDirs = new int[2];
        for (Coordinate c : paths) {
            int[] flows = tileMap.getFlows(c);
            int num = getFlowDirs(flows, flowDirs);
            if (num == 2 && isLinear(flowDirs[0], flowDirs[1])) {
                drawPath(spriteBatch, c, flows, flowDirs);
            }
        }
    }

    public void drawCrosses(SpriteBatch spriteBatch) {
        int[] flowDirs = new int[2];
        for (Coordinate c : crosses) {
            drawCross(spriteBatch, c);
        }
    }

    private static final int UP    = 0;
    private static final int RIGHT = 1;
    private static final int DOWN  = 2;
    private static final int LEFT  = 3;

    boolean canDraw(Coordinate c) {
        return tileMap.isInsideMap(c) && tileMap.getTileType(c) == TileType.PATH;
    }

    static int getFlowDirs(int[] flows, int[] out) {
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

    static boolean isLinear(int d1, int d2) {
        return (d1 == UP && d2 == DOWN) || (d1 == DOWN && d2 == UP) ||
               (d1 == LEFT && d2 == RIGHT) || (d1 == RIGHT && d2 == LEFT);
    }

    static float getRotationFromDirection(int dir) {
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
