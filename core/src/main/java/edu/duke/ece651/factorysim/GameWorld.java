package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;
import java.util.function.*;

/**
 * Represents a central game world manager that manages resources and other actors.
 * Instances need to be explicitly disposed with `dispose` method after use.
 */
public class GameWorld implements Disposable, InputProcessor {
    // Dimension
    private final int cellSize;

    // View
    private final OrthographicCamera camera;
    private final Viewport viewport;

    // Simulation
    private final Simulation sim;
    private final Logger logger;

    // Resources
    private final Texture cellTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final Texture pathTexture;
    private final Texture pathCrossTexture;
    private final Texture selectTexture;
    private final Texture selectMineTexture;
    private final Texture selectFactoryTexture;
    private final Texture selectStorageTexture;
    private final Texture selectFromTexture;
    private final Texture selectToTexture;

    // Animation
    private final Animation<TextureRegion> mineAnimation;
    private final Animation<TextureRegion> factoryAnimation;
    private final Animation<TextureRegion> storageAnimation;
    private final Animator<TextureRegion> pathAnimator;

    // Actors
    private final GridActor grid;
    private final List<BuildingActor> buildings = new ArrayList<>();
    private final Map<Coordinate, BuildingActor> buildingMap = new HashMap<>();
    private final List<PathActor> paths = new ArrayList<>();

    /**
     * Constructs a `WorldActor` instance based on grid dimension.
     *
     * @param gridCols number of columns in the grid.
     * @param gridRows number of rows in the grid.
     */
    public GameWorld(int gridCols, int gridRows, int cellSize,
                     OrthographicCamera camera, Viewport viewport,
                     float x, float y) {
        // Set and calculate dimensions
        this.cellSize = cellSize;
        int width = gridCols * cellSize;
        int height = gridRows * cellSize;

        // Set camera and viewport
        this.camera = camera;
        this.viewport = viewport;

        // Load textures
        this.cellTexture = new Texture("cell.png");
        this.mineTexture = new Texture("mine.png");
        this.factoryTexture = new Texture("factory.png");
        this.storageTexture = new Texture("storage.png");
        this.pathTexture = new Texture("path.png");
        this.pathCrossTexture = new Texture("path_cross.png");
        this.selectTexture = new Texture("select.png");
        this.selectMineTexture = new Texture("select_mine.png");
        this.selectFactoryTexture = new Texture("select_factory.png");
        this.selectStorageTexture = new Texture("select_storage.png");
        this.selectFromTexture = new Texture("select_from.png");
        this.selectToTexture = new Texture("select_to.png");

        // Create animations
        this.mineAnimation = createAnimation(mineTexture, mineTexture.getHeight(),
            mineTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.factoryAnimation = createAnimation(factoryTexture, factoryTexture.getHeight() / 2,
            factoryTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.storageAnimation = createAnimation(storageTexture, storageTexture.getHeight(),
            storageTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.pathAnimator = new Animator<>(createAnimation(pathTexture, pathTexture.getHeight(),
            pathTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP), true);

        // Create empty world and simulation
        // TODO: Replace with the GUI logger
        this.logger = new StreamLogger(System.out);
        this.sim = new Simulation(WorldBuilder.buildEmptyWorld(gridCols, gridRows), 0, logger);

        // Create the grid
        this.grid = new GridActor(gridCols, gridRows, cellSize, this.cellTexture, this.selectTexture,
            x - (width / 2f), y - (height / 2f));
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

    public void loadSimulation(String json) {
        // Update the simulation based on the JSON string
        sim.loadFromJsonString(json);
        World world = sim.getWorld();
        TileMap tileMap = world.getTileMap();

        // Resize the grid
        grid.resize(tileMap.getWidth(), tileMap.getHeight());

        // Release actors associated with the previous simulation
        buildings.clear();
        buildingMap.clear();
        paths.clear();

        // Create new actors
        for (Building building : world.getBuildings()) {
            addBuilding(building);
        }
    }

    /**
     * Render the game world.
     *
     * @param spriteBatch is the `SpriteBatch` instance used to render.
     * @param dt is the delta time (time passed since last render).
     */
    public void render(SpriteBatch spriteBatch, float dt) {
        // Draw background grid
        grid.draw(spriteBatch);

        // Draw paths
        pathAnimator.step(dt);
        for (PathActor path : paths) {
            path.draw(spriteBatch);
        }

        // Draw buildings
        for (BuildingActor building : buildings) {
            building.draw(spriteBatch);
        }

        // Draw grid selection box
        grid.drawSelectionBox(spriteBatch);
    }

    @Override
    public void dispose() {
        cellTexture.dispose();
        mineTexture.dispose();
        factoryTexture.dispose();
        storageTexture.dispose();
        pathTexture.dispose();
        pathCrossTexture.dispose();
        selectTexture.dispose();
        selectMineTexture.dispose();
        selectFactoryTexture.dispose();
        selectStorageTexture.dispose();
        selectFromTexture.dispose();
        selectToTexture.dispose();
    }



    private Vector2 screenToWorld(Vector2 screenPos) {
        viewport.unproject(screenPos);
        return screenPos;
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
        // Make sure the location is not occupied by other buildings
        if (sim.getWorld().isOccupied(coordinate)) {
            throw new IllegalArgumentException("The location " + coordinate + " is occupied by another building");
        }

        // Make sure the location is not occupied by paths
        if (!sim.getWorld().getTileMap().isAvailable(coordinate)) {
            throw new IllegalArgumentException("The location " + coordinate + " is occupied by a path");
        }

        // Set location
        building.setLocation(coordinate);

        // Add the building to the world
        if (!sim.getWorld().tryAddBuilding(building)) {
            throw new IllegalArgumentException("Failed when trying to build at location " + coordinate);
        }

        // Construct and add the actor
        return addBuilding(building, animation);
    }

    /**
     * Creates an actor for the building and add it to the game world.
     *
     * @param building is the building to add.
     * @param animation is the animation of the building's actor.
     * @return constructed building actor.
     */
    private BuildingActor addBuilding(Building building, Animation<TextureRegion> animation) {
        Coordinate location = building.getLocation();
        Vector2 worldPos = coordinateToWorld(location);
        BuildingActor actor = new BuildingActor(building, animation, worldPos.x, worldPos.y);
        buildings.add(actor);
        buildingMap.put(location, actor);
        return actor;
    }

    /**
     * Creates an actor for the building and add it to the game world.
     * Let the method choose the animation based on the building type.
     *
     * @param building is the building to add.
     * @return constructed building actor.
     * @throws IllegalArgumentException when building type has no corresponding animation available.
     */
    private BuildingActor addBuilding(Building building) {
        if (building instanceof MineBuilding) {
            return addBuilding(building, mineAnimation);
        }
        if (building instanceof FactoryBuilding) {
            return addBuilding(building, factoryAnimation);
        }
        if (building instanceof StorageBuilding) {
            return addBuilding(building, storageAnimation);
        }
        throw new IllegalArgumentException("Unsupported building type: " + building.getClass().getName());
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
        name = sim.getWorld().resolveBuildingNameConflict(name);
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
        name = sim.getWorld().resolveBuildingNameConflict(name);
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
        name = sim.getWorld().resolveBuildingNameConflict(name);
        StorageBuilding factory = new StorageBuilding(name, new ArrayList<>(), sim, storageItem, maxCapacity, priority);
        return buildBuilding(factory, storageAnimation, coordinate);
    }

    public PathActor connectPath(BuildingActor from, BuildingActor to) {
        // Connect the two buildings
        Path path = sim.connectBuildings(from.getBuilding(), to.getBuilding());
        if (path == null) {
            throw new IllegalArgumentException("Cannot connect " + from.getBuilding().getName() + " to " + to.getBuilding().getName() + ": No valid path");
        }

        // Create the actor
        PathActor actor = new PathActor(path, sim.getWorld().getTileMap(), pathAnimator, pathCrossTexture,
            this::coordinateToWorld);
        paths.add(actor);
        return actor;
    }

    public BuildingActor getBuildingAt(Coordinate c) {
        return buildingMap.get(c);
    }



    public interface Phase {
        void onClick(Coordinate c);
        void onEnter();
    }

    public class DefaultPhase implements Phase {
        @Override
        public void onClick(Coordinate c) { }

        @Override
        public void onEnter() {
            grid.setSelectTexture(selectTexture);
        }
    }

    public abstract class BuildPhase implements Phase {
        private final Texture selectTexture;

        protected final String name;

        protected BuildPhase(Texture selectTexture, String name) {
            this.selectTexture = selectTexture;
            this.name = name;
        }

        @Override
        public abstract void onClick(Coordinate c);

        @Override
        public void onEnter() {
            grid.setSelectTexture(selectTexture);
        }
    }

    public class BuildMinePhase extends BuildPhase {
        private final Recipe miningRecipe;

        public BuildMinePhase(String name, Recipe miningRecipe) {
            super(selectMineTexture, name);
            this.miningRecipe = miningRecipe;
        }

        @Override
        public void onClick(Coordinate c) {
            try {
                buildMine(name, miningRecipe, c);
            } catch (Exception e) {
                logger.log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class BuildFactoryPhase extends BuildPhase {
        private final Type factoryType;

        public BuildFactoryPhase(String name, Type factoryType) {
            super(selectFactoryTexture, name);
            this.factoryType = factoryType;
        }

        @Override
        public void onClick(Coordinate c) {
            try {
                buildFactory(name, factoryType, c);
            } catch (Exception e) {
                logger.log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class BuildStoragePhase extends BuildPhase {
        private final Item storageItem;
        private final int maxCapacity;
        private final double priority;

        public BuildStoragePhase(String name, Item storageItem, int maxCapacity, double priority) {
            super(selectStorageTexture, name);
            this.storageItem = storageItem;
            this.maxCapacity = maxCapacity;
            this.priority = priority;
        }

        @Override
        public void onClick(Coordinate c) {
            try {
                buildStorage(name, storageItem, maxCapacity, priority, c);
            } catch (Exception e) {
                logger.log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class ConnectPhase implements Phase {
        private BuildingActor from = null;

        @Override
        public void onClick(Coordinate c) {
            try {
                // Get source if not already
                if (from == null) {
                    from = getBuildingAt(c);
                    if (from != null) {
                        grid.setSelectTexture(selectToTexture); // Update select box texture
                    }
                    return; // Intentional. If clicked on a coordinate with no building, do nothing
                }

                // Get destination
                BuildingActor to = getBuildingAt(c);
                if (to == null) {
                    return;
                }

                // Try to connect
                connectPath(from, to);
                enterDefaultPhase(); // Connected successfully, resume to default phase
            } catch (Exception e) {
                // Log error and resume back to default phase on error
                logger.log(e.getMessage());
                enterDefaultPhase();
            }
        }

        @Override
        public void onEnter() {
            grid.setSelectTexture(selectFromTexture);
        }
    }

    private Phase phase = new DefaultPhase();

    public void enterDefaultPhase() {
        phase = new DefaultPhase();
        phase.onEnter();
    }

    public void enterBuildMinePhase(String name, Recipe miningRecipe) {
        phase = new BuildMinePhase(name, miningRecipe);
        phase.onEnter();
    }

    public void enterBuildFactoryPhase(String name, Type factoryType) {
        phase = new BuildFactoryPhase(name, factoryType);
        phase.onEnter();
    }

    public void enterBuildStoragePhase(String name, Item storageItem, int maxCapacity, double priority) {
        phase = new BuildStoragePhase(name, storageItem, maxCapacity, priority);
        phase.onEnter();
    }

    public void enterConnectPhase() {
        phase = new ConnectPhase();
        phase.onEnter();
    }



    @Override
    public boolean keyDown(int keycode) {
        // TODO: The following is test code
        if (keycode == Input.Keys.GRAVE) { // Default
            enterDefaultPhase();
        } else if (keycode == Input.Keys.NUM_1) { // Mine
            enterBuildMinePhase("M", new Recipe(new Item("metal"), new HashMap<>(), 1));
        } else if (keycode == Input.Keys.NUM_2) { // Factory
            enterBuildFactoryPhase("Hi", new Type("Hi", List.of()));
        } else if (keycode == Input.Keys.NUM_3) { // Storage
            enterBuildStoragePhase("St", new Item("metal"), 10, 1.0);
        } else if (keycode == Input.Keys.NUM_4) { // Connect
            enterConnectPhase();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            // Get coordinate based on touch screen position
            Vector2 pos = screenToWorld(new Vector2(screenX, screenY));
            Coordinate c = worldToCoordinate(pos);

            // Invoke current phase's `onClick` event
            phase.onClick(c);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    private final Vector2 tempVec2 = new Vector2();

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        tempVec2.set(screenX, screenY);
        grid.onMouseMoved(screenToWorld(tempVec2));
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
