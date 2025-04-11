package edu.duke.ece651.factorysim;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.util.*;

/**
 * Represents a central game world manager that manages resources and other actors.
 * Instances need to be explicitly disposed with `dispose` method after use.
 */
public class WorldActor extends Actor2D implements Disposable {
    // Dimension
    private final int cellSize;

    // Simulation
    private final Simulation sim;

    // Resources
    private final Texture gridTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final Texture pathTexture;
    private final Texture pathCornerTexture;

    private final Animation<TextureRegion> mineAnimation;
    private final Animation<TextureRegion> factoryAnimation;
    private final Animation<TextureRegion> storageAnimation;
    private final Animation<TextureRegion> pathAnimation;
    private final Animation<TextureRegion> pathCornerAnimation;

    // Actors
    private final GridActor grid;
    private final List<BuildingActor> buildings = new ArrayList<>();
    private final List<PathActor> paths = new ArrayList<>();

    /**
     * Constructs a `WorldActor` instance based on grid dimension.
     *
     * @param gridCols number of columns in the grid.
     * @param gridRows number of rows in the grid.
     */
    public WorldActor(int gridCols, int gridRows, int cellSize, float x, float y) {
        super(x, y);

        this.cellSize = cellSize;
        int width = gridCols * cellSize;
        int height = gridRows * cellSize;

        // Load textures
        this.gridTexture = new Texture("cell.png");
        this.mineTexture = new Texture("mine.png");
        this.factoryTexture = new Texture("factory.png");
        this.storageTexture = new Texture("storage.png");
        this.pathTexture = new Texture("path.png");
        this.pathCornerTexture = new Texture("path_corner.png");

        // Create animations
        this.mineAnimation = createAnimation(mineTexture, mineTexture.getHeight(),
            mineTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.factoryAnimation = createAnimation(factoryTexture, factoryTexture.getHeight() / 2,
            factoryTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.storageAnimation = createAnimation(storageTexture, storageTexture.getHeight(),
            storageTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.pathAnimation = createAnimation(pathTexture, pathTexture.getHeight(),
            pathTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.pathCornerAnimation = createAnimation(pathCornerTexture, pathCornerTexture.getHeight(),
            pathCornerTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);

        // Create empty world and simulation
        // TODO: Replace with GUI logger
        this.sim = new Simulation(buildEmptyWorld(gridCols, gridRows), 0, new StreamLogger(System.out));

        // Create the grid
        this.grid = new GridActor(gridCols, gridRows, this.gridTexture, x - (width / 2f), y - (height / 2f));
    }

    /**
     * Splits a texture into frames based on frame dimension, then creates an animation from it.
     *
     * @param texture is the texture to split frames from.
     * @param frameWidth is the width of each frame.
     * @param frameHeight is the height of each frame.
     * @param frameDuration is the duration of each frame in the animation.
     * @param playMode is the animation's play mode.
     * @return animation created from the texture.
     * @throws IllegalArgumentException when failed to split any frames from the texture and frame dimensions.
     */
    private static Animation<TextureRegion> createAnimation(Texture texture, int frameWidth, int frameHeight,
                                                            float frameDuration, Animation.PlayMode playMode) {
        TextureRegion[][] regions = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = Arrays.stream(regions)
                                       .flatMap(Arrays::stream)
                                       .toArray(TextureRegion[]::new);
        if (frames.length == 0) {
            throw new IllegalArgumentException("Failed to split any animation frame from the texture");
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    /**
     * Helper function that creates an empty `World` instance.
     *
     * @param boardWidth is the width of the board used by `TileMap`.
     * @param boardHeight is the height of the board used by `TileMap`.
     * @return a newly constructed empty `World` instance.
     */
    private static World buildEmptyWorld(int boardWidth, int boardHeight) {
        World world = new World();
        world.setTypes(new ArrayList<>());
        world.setRecipes(new ArrayList<>());
        world.setBuildings(new ArrayList<>());
        world.setTileMapDimensions(boardWidth, boardHeight);
        return world;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        // Draw background grid
        grid.draw(spriteBatch);

        // Draw paths
        for (PathActor path : paths) {
            path.draw(spriteBatch);
        }

        // Draw buildings
        for (BuildingActor building : buildings) {
            building.draw(spriteBatch);
        }
    }

    @Override
    public void dispose() {
        gridTexture.dispose();
        mineTexture.dispose();
        factoryTexture.dispose();
        storageTexture.dispose();
        pathTexture.dispose();
        pathCornerTexture.dispose();
    }

    /**
     * Converts a coordinate on the grid to a global position.
     *
     * @param coordinate is the coordinate on the grid to convert.
     * @return converted global position.
     */
    public Vector2 coordinateToWorld(Coordinate coordinate) {
        int clampedX = Math.max(0, Math.min(coordinate.getX(), grid.getCols() - 1));
        int clampedY = Math.max(0, Math.min(coordinate.getY(), grid.getRows() - 1));
        return new Vector2(
            grid.position.x + clampedX * cellSize,
            grid.position.y + clampedY * cellSize
        );
    }

    /**
     * Converts a global position to a coordinate on the grid.
     *
     * @param worldPos is the global position to convert.
     * @return converted coordinate on the grid.
     */
    public Coordinate worldToCoordinate(Vector2 worldPos) {
        int x = (int)((worldPos.x - grid.position.x) / cellSize);
        int y = (int)((worldPos.y - grid.position.y) / cellSize);
        int clampedX = Math.max(0, Math.min(x, grid.getCols() - 1));
        int clampedY = Math.max(0, Math.min(y, grid.getRows() - 1));
        return new Coordinate(clampedX, clampedY);
    }

    /**
     * Constructs a `BuildingActor` and add it to the world's building list.
     *
     * @param building is the `Building` instance the actor is based on.
     * @param animation is the animation used by the building.
     * @param coordinate is the coordinate to build on.
     * @return newly constructed `BuildingActor` instance.
     */
    private BuildingActor buildBuilding(Building building, Animation<TextureRegion> animation, Coordinate coordinate) {
        // Set location
        building.setLocation(coordinate);

        // Construct and add the actor
        Vector2 worldPos = coordinateToWorld(coordinate);
        BuildingActor actor = new BuildingActor(building, animation, worldPos.x, worldPos.y);
        buildings.add(actor);
        return actor;
    }

    /**
     * Creates a `BuildingActor` that's based on a `MineBuilding`.
     *
     * @param name is the name of the mine building.
     * @param miningRecipe is the recipe used by the mine building.
     * @param coordinate is the coordinate to build on.
     * @return created `BuildingActor` reference.
     */
    public BuildingActor buildMine(String name, Recipe miningRecipe, Coordinate coordinate) {
        MineBuilding mine = new MineBuilding(miningRecipe, name, sim);
        return buildBuilding(mine, mineAnimation, coordinate);
    }

    /**
     * Creates a `BuildingActor` that's based on a `FactoryBuilding`.
     *
     * @param name is the name of the factory building.
     * @param factoryType is the type of factory.
     * @param coordinate is the coordinate to build on.
     * @return created `BuildingActor` reference.
     */
    public BuildingActor buildFactory(String name, Type factoryType, Coordinate coordinate) {
        FactoryBuilding factory = new FactoryBuilding(factoryType, name, new ArrayList<>(), sim);
        return buildBuilding(factory, factoryAnimation, coordinate);
    }

    /**
     * Creates a `BuildingActor` that's based on a `StorageBuilding`.
     *
     * @param name is the name of the storage building.
     * @param storageItem is the item in storage for this storage building.
     * @param maxCapacity is the maximum capacity number.
     * @param priority is a value to decide how aggressively should make requests for refills.
     * @param coordinate is the coordinate to build on.
     * @return created `BuildingActor` reference.
     */
    public BuildingActor buildStorage(String name, Item storageItem, int maxCapacity,
                                      double priority, Coordinate coordinate) {
        StorageBuilding factory = new StorageBuilding(name, new ArrayList<>(), sim, storageItem, maxCapacity, priority);
        return buildBuilding(factory, storageAnimation, coordinate);
    }

    public PathActor buildPath(BuildingActor from, BuildingActor to) {
        // TODO
        return null;
    }
}
