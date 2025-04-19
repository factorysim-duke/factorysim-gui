package edu.duke.ece651.factorysim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.*;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
import java.util.*;

/**
 * Represents a central game world manager that manages resources and other actors.
 * Instances need to be explicitly disposed with `dispose` method after use.
 */
public class GameWorld implements Disposable, InputProcessor, DeliveryListener {
    // Dimension
    private final int cellSize;

    // Camera & View
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private static final float CAMERA_SPEED = 200f;
    private static final float CAMERA_SPEED_MULTIPLIER = 2f;
    private final Vector2 cameraVelocity = new Vector2(0f, 0f);

    // Rendering
    private final SpriteBatch spriteBatch;

    // Zoom
    private float targetZoom = 1f;
    private static final float ZOOM_AMOUNT = 0.2f;
    private static final float ZOOM_SPEED = 5f;
    private static final float ZOOM_MIN = 0.5f;
    private static final float ZOOM_MAX = 2f;

    // Simulation
    private Simulation sim;

    public Simulation getSim() { return this.sim; }

    // Real-time
    private RealTimeSimulation realTime;
    private boolean realTimeEnabled;

    public RealTimeSimulation getRealTime() { return this.realTime; }

    public boolean isRealTimeEnabled() { return this.realTimeEnabled; }
    public void enableRealTime() { this.realTimeEnabled = true; }
    public void disableRealTime() { this.realTimeEnabled = false; }

    // Resources
    private final Texture cellTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final Texture itemTexture;
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
    private final List<BuildingActor> buildingActors = new ArrayList<>();
    private final Map<Coordinate, BuildingActor> buildingMap = new HashMap<>();
    private final List<Tuple<PathActor, Path>> pathPairs = new ArrayList<>();
    private final List<Coordinate> pathCrossCoords = new ArrayList<>();
    private final List<DeliveryActor> deliveries = new ArrayList<>();

    // Screen
    private final SimulationScreen screen;

    /**
     * Constructs a `WorldActor` instance based on grid dimension.
     *
     * @param gridCols number of columns in the grid.
     * @param gridRows number of rows in the grid.
     */
    public GameWorld(int gridCols, int gridRows, int cellSize,
                     Logger logger,
                     SimulationScreen screen,
                     float x, float y) {
        // Set and calculate dimensions
        this.cellSize = cellSize;
        int width = gridCols * cellSize;
        int height = gridRows * cellSize;

        // Set up camera & viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
        camera.position.set(0f, 0f, 0f);
        camera.update();
        viewport.apply();

        // Create sprite batch for rendering
        this.spriteBatch = new SpriteBatch();

        // Load textures
        this.cellTexture = new Texture("cell.png");
        this.mineTexture = new Texture("mine.png");
        this.factoryTexture = new Texture("factory.png");
        this.storageTexture = new Texture("storage.png");
        this.itemTexture = new Texture("item.png");
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
        this.sim = new Simulation(WorldBuilder.buildEmptyWorld(gridCols, gridRows), 0, logger);
        this.sim.deliverySchedule.subscribe(this);
        this.realTime = new RealTimeSimulation(this.sim);

        // Create the grid
        this.grid = new GridActor(gridCols, gridRows, cellSize, this.cellTexture, this.selectTexture,
            x - (width / 2f), y - (height / 2f));

        // Set screen
        this.screen = screen;

        // Initialize camera zoom
        updateTargetZoom(targetZoom);
        syncZoom();
    }

    /**
     * Sets a new `Simulation` instance to the game world. Updates everything in the world.
     *
     * @param sim is the new `Simulation` instance to set.
     */
    public void setSimulation(Simulation sim) {
        // Unsubscribe as a listener from the previous simulation
        this.sim.getDeliverySchedule().unsubscribe(this);

        // Set new simulation
        this.sim = sim;
        this.sim.getDeliverySchedule().subscribe(this);
        this.realTime = new RealTimeSimulation(this.sim);
        World world = sim.getWorld();
        TileMap tileMap = world.getTileMap();

        // Resize the grid
        grid.resize(tileMap.getWidth(), tileMap.getHeight());

        // Sync camera zoom instantly
        syncZoom();

        // Release actors associated with the previous simulation
        buildingActors.clear();
        buildingMap.clear();
        pathPairs.clear();
        pathCrossCoords.clear();

        // Create building actors
        for (Building building : world.getBuildings()) {
            BuildingActor actor = actorizeBuilding(building);
            buildingMap.put(building.getLocation(), actor);
        }

        // Create path actors
        for (BuildingActor buildingActor : buildingActors) {
            Building building = buildingActor.getBuilding();

            // Connect sources to building
            for (Building source : building.getSources()) {
                Optional<BuildingActor> result = buildingActors.stream()
                    .filter((a) -> a.getBuilding() == source)
                    .findAny();
                result.ifPresent((sourceActor) -> {
                    connectPath(sourceActor, buildingActor);
                });
            }
        }
    }

    /**
     * Gets the current logger instance used by the `GameWorld` instance.
     *
     * @return the current logger instance used by the `GameWorld` instance.
     */
    public Logger getLogger() { return this.sim.getLogger(); }

    /**
     * Sets a new logger for the `GameWorld` instance.
     *
     * @param logger is the new logger.
     */
    public void setLogger(Logger logger) {
        this.sim.setLogger(logger);
    }

    public void log(String s) {
        this.getLogger().log(s);
    }

    /**
     * Update target zoom to a new value while making sure it won't exceed bounds.
     *
     * @param newZoom is the new target zoom.
     */
    private void updateTargetZoom(float newZoom) {
        newZoom = MathUtils.clamp(newZoom, ZOOM_MIN, ZOOM_MAX);
        targetZoom = Math.min(newZoom, calculateZoomLimit());
    }

    /**
     * Sync camera zoom to target zoom instantly.
     */
    private void syncZoom() {
        camera.zoom = targetZoom;
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
     * Update the game world.
     *
     * @param dt is the delta time (time passed since last update).
     */
    public void update(float dt) {
        // Update camera
        viewport.apply();
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Zoom
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, ZOOM_SPEED * dt);

        // Handle camera movement
        handleCameraMovement(dt);

        // Real-time
        if (realTimeEnabled) {
            realTime.update(dt);
        }

        // Rendering
        render(dt);
    }

    private void render(float dt) {
        spriteBatch.begin();

        // Draw background grid
        grid.drawGrid(spriteBatch);

        // Draw buildings
        for (BuildingActor building : buildingActors) {
            building.draw(spriteBatch);
        }

        // Draw paths
        pathAnimator.step(dt);
        for (Tuple<PathActor, Path> tuple : pathPairs) {
            tuple.first().drawPaths(spriteBatch);
        }

        // Draw items
        for (DeliveryActor delivery : deliveries) {
            delivery.draw(spriteBatch);
        }

        // Draw crossing paths
        if (!pathPairs.isEmpty()) {
            pathPairs.getFirst().first().drawCrosses(spriteBatch, pathCrossCoords);
        }

        // Draw grid selection box
        grid.drawSelectionBox(spriteBatch);

        spriteBatch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    private void handleCameraMovement(float dt) {
        // Camera movement
        cameraVelocity.set(0f, 0f);
        if (isHoldingUp) {
            cameraVelocity.y = 1f;
        } else if (isHoldingDown) {
            cameraVelocity.y = -1f;
        }
        if (isHoldingLeft) {
            cameraVelocity.x = -1f;
        } else if (isHoldingRight) {
            cameraVelocity.x = 1f;
        }
        cameraVelocity.nor().scl(CAMERA_SPEED * (isHoldingSpeed ? CAMERA_SPEED_MULTIPLIER : 1f) * camera.zoom * dt);
        camera.position.add(cameraVelocity.x, cameraVelocity.y, 0f);

        // Invoke mouse movement event if camera is moved
        if (cameraVelocity.x != 0f || cameraVelocity.y != 0f) {
            mouseMoved(mouseScreenX, mouseScreenY);
        }

        // Make sure camera don't go off the world
        float vwHalf = camera.viewportWidth * camera.zoom / 2f;
        float vhHalf = camera.viewportHeight * camera.zoom / 2f;
        camera.position.x = MathUtils.clamp(camera.position.x,
            grid.position.x + vwHalf, grid.position.x + grid.getWidth() - vwHalf);
        camera.position.y = MathUtils.clamp(camera.position.y,
            grid.position.y + vhHalf, grid.position.y + grid.getHeight() - vhHalf);
    }

    @Override
    public void dispose() {
        // Dispose sprite batch
        spriteBatch.dispose();

        // Dispose textures
        cellTexture.dispose();
        mineTexture.dispose();
        factoryTexture.dispose();
        storageTexture.dispose();
        itemTexture.dispose();
        pathTexture.dispose();
        pathCrossTexture.dispose();
        selectTexture.dispose();
        selectMineTexture.dispose();
        selectFactoryTexture.dispose();
        selectStorageTexture.dispose();
        selectFromTexture.dispose();
        selectToTexture.dispose();
    }



    /**
     * Converts a position on the screen to where it is in the world.
     *
     * @param screenPos is the screen position to convert.
     * @return converted world position of the screen position.
     */
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
        return actorizeBuilding(building, animation);
    }

    /**
     * Creates an actor for the building and add it to the game world.
     *
     * @param building is the building to add.
     * @param animation is the animation of the building's actor.
     * @return constructed building actor.
     */
    private BuildingActor actorizeBuilding(Building building, Animation<TextureRegion> animation) {
        Coordinate location = building.getLocation();
        Vector2 worldPos = coordinateToWorld(location);
        BuildingActor actor = new BuildingActor(building, animation, worldPos.x, worldPos.y);
        buildingActors.add(actor);
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
    private BuildingActor actorizeBuilding(Building building) {
        if (building instanceof MineBuilding) {
            return actorizeBuilding(building, mineAnimation);
        }
        if (building instanceof FactoryBuilding) {
            return actorizeBuilding(building, factoryAnimation);
        }
        if (building instanceof StorageBuilding) {
            return actorizeBuilding(building, storageAnimation);
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

    /**
     * Takes an existing path and creates an actor from it.
     *
     * @param path is the path instance to be an actor.
     * @return constructed `PathActor` instance.
     */
    private PathActor actorizePath(Path path) {
        // Create actor
        PathActor actor = new PathActor(path, sim.getWorld().getTileMap(), pathAnimator, pathCrossTexture,
            this::coordinateToWorld);
        pathPairs.add(new Tuple<>(actor, path));

        // Cache and sort paths
        for (Coordinate c : actor.getCrosses()) {
            pathCrossCoords.add(c);
        }
        pathCrossCoords.sort((a, b) -> {
            float ay = coordinateToWorld(a).y;
            float by = coordinateToWorld(b).y;
            return Float.compare(by, ay);
        });

        return actor;
    }

    public void connectPath(BuildingActor from, BuildingActor to) {
        // Connect the two buildings
        Path path = sim.connectBuildings(from.getBuilding(), to.getBuilding());
        if (path == null) {
            throw new IllegalArgumentException("Cannot connect " + from.getBuilding().getName() + " to " + to.getBuilding().getName() + ": No valid path");
        }
        if (pathPairs.stream().anyMatch((t) -> t.second() == path)) {
            return;
        }

        // TODO: Add 'from' as a new source of 'to'
//        Building source = from.getBuilding();
//        Building target = to.getBuilding();

        // Create the actor
        actorizePath(path);
    }

    /**
     * Gets the building actor at a certain coordinate.
     *
     * @param c is the coordinate to get the actor.
     * @return the building actor instance at that coordinate or null.
     */
    public BuildingActor getBuildingAt(Coordinate c) {
        return buildingMap.get(c);
    }


    public interface Phase {
        default void onClick(Coordinate c) { }
        default void onRelease(Coordinate c) { }
        default void onEnter() { }
    }

    public class DefaultPhase implements Phase {
        @Override
        public void onClick(Coordinate c) {
            BuildingActor actor = getBuildingAt(c);
            if (actor == null) {
                return;
            }
            screen.showBuildingInfo(actor.getBuilding());
        }

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
                log(e.getMessage());
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
                log(e.getMessage());
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
                log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class ConnectPhase implements Phase {
        private BuildingActor from = null;

        private void onConnect(Coordinate c) {
            // Get destination
            BuildingActor to = getBuildingAt(c);
            if (to == null) {
                return;
            }

            // Try to connect
            connectPath(from, to);
            enterDefaultPhase(); // Connected successfully, resume to default phase
        }

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

                // Try to connect to the coordinate
                onConnect(c);
            } catch (Exception e) {
                // Log error and resume back to default phase on error
                log(e.getMessage());
                enterDefaultPhase();
            }
        }

        @Override
        public void onRelease(Coordinate c) {
            try {
                // Ignore if no source or release coordinate is where source is
                if (from == null || from.getBuilding().getLocation().equals(c)) {
                    return;
                }

                // Try to connect to the coordinate
                onConnect(c);
            } catch (Exception e) {
                // Log error and resume back to default phase on error
                log(e.getMessage());
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



    private boolean isHoldingUp = false;
    private boolean isHoldingDown = false;
    private boolean isHoldingLeft = false;
    private boolean isHoldingRight = false;
    private boolean isHoldingSpeed = false;

    @Override
    public boolean keyDown(int keycode) {
        // Movement down
        if (keycode == Input.Keys.W) {
            isHoldingUp = true;
        }
        if (keycode == Input.Keys.S) {
            isHoldingDown = true;
        }
        if (keycode == Input.Keys.A) {
            isHoldingLeft = true;
        }
        if (keycode == Input.Keys.D) {
            isHoldingRight = true;
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            isHoldingSpeed = true;
        }

        // Tool selection
        if (keycode == Input.Keys.NUM_1) { // Default
            enterDefaultPhase();
        } else if (keycode == Input.Keys.NUM_2) { // Mine
            enterBuildMinePhase("M", new Recipe(new Item("metal"), new HashMap<>(), 1));
        } else if (keycode == Input.Keys.NUM_3) { // Factory
            enterBuildFactoryPhase("Hi", new Type("hinge", List.of()));
        } else if (keycode == Input.Keys.NUM_4) { // Storage
            enterBuildStoragePhase("St", new Item("metal"), 10, 1.0);
        } else if (keycode == Input.Keys.NUM_5) { // Connect
            enterConnectPhase();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Movement up
        if (keycode == Input.Keys.W) {
            isHoldingUp = false;
        }
        if (keycode == Input.Keys.S) {
            isHoldingDown = false;
        }
        if (keycode == Input.Keys.A) {
            isHoldingLeft = false;
        }
        if (keycode == Input.Keys.D) {
            isHoldingRight = false;
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            isHoldingSpeed = false;
        }

        return true;
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
        if (button == Input.Buttons.LEFT) {
            // Get coordinate based on touch screen position
            Vector2 pos = screenToWorld(new Vector2(screenX, screenY));
            Coordinate c = worldToCoordinate(pos);

            // Invoke current phase's `onRelease` event
            phase.onRelease(c);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    private int mouseScreenX = 0;
    private int mouseScreenY = 0;
    private final Vector2 mouseWorldPos = new Vector2(0f, 0f);

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseScreenX = screenX;
        mouseScreenY = screenY;
        mouseWorldPos.set(screenX, screenY);
        grid.onMouseMoved(screenToWorld(mouseWorldPos));
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        updateTargetZoom(targetZoom + amountY * ZOOM_AMOUNT);
        return true;
    }

    /**
     * Calculates a zoom limit based on the grid size and viewport size.
     * A zoom limit is the maximum zoom that makes sure all grid is shown while no out-of-bound is visible.
     *
     * @return the camera zoom limit.
     */
    private float calculateZoomLimit() {
        float zoomMaxX = grid.getWidth() / viewport.getWorldWidth();
        float zoomMaxY = grid.getHeight() / viewport.getWorldHeight();
        return Math.min(zoomMaxX, zoomMaxY);
    }

    @Override
    public void onDeliveryAdded(Delivery delivery) {
        deliveries.add(new DeliveryActor(delivery, itemTexture, this::coordinateToWorld));
    }

    @Override
    public void onDeliveryFinished(Delivery delivery) {
        deliveries.removeIf((d) -> d.getDelivery() == delivery);
    }
}
