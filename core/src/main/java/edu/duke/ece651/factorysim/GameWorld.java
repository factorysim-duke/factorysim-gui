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
    private boolean realTimeEnabled = false;

    public RealTimeSimulation getRealTime() { return this.realTime; }

    public boolean isRealTimeEnabled() { return this.realTimeEnabled; }
    public void enableRealTime() { this.realTimeEnabled = true; }
    public void disableRealTime() { this.realTimeEnabled = false; }

    // Resources
    private final Texture cellTexture;
    private final Texture mineTexture;
    private final Texture factoryTexture;
    private final Texture storageTexture;
    private final Texture dronePortTexture;
    private final Texture wasteDisposalTexture;

    private final Texture itemTexture;
    private final Texture droneTexture;

    private final Texture pathTexture;
    private final Texture pathCrossTexture;

    private final Texture selectTexture;
    private final Texture selectMineTexture;
    private final Texture selectFactoryTexture;
    private final Texture selectStorageTexture;
    private final Texture selectDronePortTexture;
    private final Texture selectWasteDisposalTexture;
    private final Texture selectFromTexture;
    private final Texture selectToTexture;

    private final Texture removeTexture;

    // Animation
    private final Animation<TextureRegion> mineAnimation;
    private final Animation<TextureRegion> factoryAnimation;
    private final Animation<TextureRegion> storageAnimation;
    private final Animation<TextureRegion> dronePortAnimation;
    private final Animation<TextureRegion> wasteDisposalAnimation;

    private final Animator<TextureRegion> pathAnimator;

    private final Animation<TextureRegion> droneAnimation;

    // Path
    private static class PathEntry {
        public PathActor actor;
        public Path path;
        public BuildingActor from;
        public BuildingActor to;

        public PathEntry(PathActor actor, Path path, BuildingActor from, BuildingActor to) {
            this.actor = actor;
            this.path = path;
            this.from = from;
            this.to = to;
        }
    }

    // Actors
    private final GridActor grid;
    private final List<BuildingActor> buildingActors = new ArrayList<>();
    private final Map<Coordinate, BuildingActor> buildingMap = new HashMap<>();
    private final List<PathEntry> pathEntries = new ArrayList<>();
    private final List<Coordinate> pathCrossCoords = new ArrayList<>();
    private final List<DeliveryActor> deliveries = new ArrayList<>();
    private final List<DroneDeliveryActor> droneDeliveries = new ArrayList<>();

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
        this.dronePortTexture = new Texture("droneport.png");
        this.wasteDisposalTexture = new Texture("wastedisposal.png");

        this.itemTexture = new Texture("item.png");
        this.droneTexture = new Texture("drone.png");

        this.pathTexture = new Texture("path.png");
        this.pathCrossTexture = new Texture("path_cross.png");

        this.selectTexture = new Texture("select.png");
        this.selectMineTexture = new Texture("select_mine.png");
        this.selectFactoryTexture = new Texture("select_factory.png");
        this.selectStorageTexture = new Texture("select_storage.png");
        this.selectDronePortTexture = new Texture("select_droneport.png");
        this.selectWasteDisposalTexture = new Texture("select_wastedisposal.png");
        this.selectFromTexture = new Texture("select_from.png");
        this.selectToTexture = new Texture("select_to.png");

        this.removeTexture = new Texture("remove.png");

        // Create animations
        this.mineAnimation = createAnimation(mineTexture, mineTexture.getHeight(),
            mineTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.factoryAnimation = createAnimation(factoryTexture, factoryTexture.getHeight() / 2,
            factoryTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.storageAnimation = createAnimation(storageTexture, storageTexture.getHeight(),
            storageTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.dronePortAnimation = createAnimation(dronePortTexture, dronePortTexture.getHeight() / 2,
            dronePortTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP);
        this.wasteDisposalAnimation = createAnimation(wasteDisposalTexture,
            wasteDisposalTexture.getHeight() / 2, wasteDisposalTexture.getHeight(), 0.1f,
            Animation.PlayMode.LOOP);

        this.pathAnimator = new Animator<>(createAnimation(pathTexture, pathTexture.getHeight(),
            pathTexture.getHeight(), 0.1f, Animation.PlayMode.LOOP), true);

        this.droneAnimation = createAnimation(droneTexture, droneTexture.getHeight(),
            droneTexture.getHeight(), 0.025f, Animation.PlayMode.LOOP);

        // Create the grid
        this.grid = new GridActor(gridCols, gridRows, cellSize, this.cellTexture, this.selectTexture,
            x - (width / 2f), y - (height / 2f));

        // Set screen
        this.screen = screen;

        // Initialize camera zoom
        updateTargetZoom(targetZoom);
        syncZoom();

        // Create empty world and simulation
        this.setSimulation(new Simulation(WorldBuilder.buildEmptyWorld(gridCols, gridRows), 0, logger));
    }

    /**
     * Sets a new `Simulation` instance to the game world. Updates everything in the world.
     *
     * @param sim is the new `Simulation` instance to set.
     */
    public void setSimulation(Simulation sim) {
        // Unsubscribe as a listener from the previous simulation
        if (this.sim != null) {
            this.sim.getDeliverySchedule().unsubscribe(this);
            this.sim.unsubscribeToOnBuildingRemoved(this::onBuildingRemoved);
        }

        // Set new simulation
        this.sim = sim;
        this.realTime = new RealTimeSimulation(this.sim);
        this.realTimeEnabled = false;
        World world = sim.getWorld();
        TileMap tileMap = world.getTileMap();

        // Subscribe to events
        this.sim.getDeliverySchedule().subscribe(this);
        this.sim.subscribeToOnBuildingRemoved(this::onBuildingRemoved);

        // Resize the grid
        grid.resize(tileMap.getWidth(), tileMap.getHeight());

        // Sync camera zoom instantly
        syncZoom();

        // Release actors associated with the previous simulation
        buildingActors.clear();
        buildingMap.clear();
        pathEntries.clear();
        pathCrossCoords.clear();
        deliveries.clear();
        droneDeliveries.clear();

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

        // Create delivery actors
        for (Delivery delivery : sim.getDeliverySchedule().deliveryList) {
            onDeliveryAdded(delivery);
        }

        // Focus on a building actor
        if (!buildingActors.isEmpty()) {
            Vector2 focus = coordinateToWorld(buildingActors.getFirst().getBuilding().getLocation());
            setCameraPosition(focus.x, focus.y);
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
            try {
                realTime.update(dt);
            } catch (Exception e) {
                log(e.getMessage());
            }
        }

        // Update deliveries
        if (realTimeEnabled) {
            if (!realTime.isPaused() && realTime.isRunning()) {
                for (DeliveryActor delivery : deliveries) {
                    delivery.update(dt, realTime.getSpeed());
                }
            }
        } else {
            for (DeliveryActor delivery : deliveries) {
                delivery.update(dt, Float.MAX_VALUE);
            }
        }

        // Update drone deliveries
        for (DroneDeliveryActor droneDelivery : droneDeliveries) {
            droneDelivery.update(dt, realTime.getSpeed(), realTimeEnabled);
        }

        // Update buildings
        for (BuildingActor buildingActor : buildingActors) {
            buildingActor.update(dt);
        }

        // Release arrived delivery actors
        deliveries.removeIf(DeliveryActor::hasArrived);
    }

    public void step(int n) {
        // Step the simulation
        sim.step(n);

        for (int i = 0; i < n; i++) {
            // Step drone actors
            if (!realTimeEnabled) {
                for (DroneDeliveryActor droneDelivery : droneDeliveries) {
                    droneDelivery.step();
                }
            }
        }
    }

    public void render(float dt) {
        spriteBatch.begin();

        // Draw background grid
        grid.drawGrid(spriteBatch);

        // Draw paths
        float pathSpeed;
        if (realTimeEnabled) {
            pathSpeed = (realTime.isPaused() || !realTime.isRunning()) ? 0f : realTime.getSpeed() * 1.625f;
        } else {
            pathSpeed = 1f;
        }
        pathAnimator.step(pathSpeed * dt);
        for (PathEntry path : pathEntries) {
            path.actor.drawPaths(spriteBatch);
        }

        // Draw package deliveries
        for (DeliveryActor delivery : deliveries) {
            delivery.draw(spriteBatch);
        }

        // Draw buildings
        for (BuildingActor building : buildingActors) {
            building.draw(spriteBatch);
        }

        // Draw crossing paths
        if (!pathEntries.isEmpty()) {
            pathEntries.getFirst().actor.drawCrosses(spriteBatch, pathCrossCoords);
        }

        // Draw drone deliveries
        for (DroneDeliveryActor droneDelivery : droneDeliveries) {
            droneDelivery.render(spriteBatch);
        }

        // Draw grid selection box
        grid.drawSelectionBox(spriteBatch);

        spriteBatch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    private void setCameraPosition(float x, float y) {
        camera.position.set(x, y, camera.position.z);

        // Make sure camera don't go off the world
        float vwHalf = camera.viewportWidth * camera.zoom / 2f;
        float vhHalf = camera.viewportHeight * camera.zoom / 2f;
        camera.position.x = MathUtils.clamp(camera.position.x,
            grid.position.x + vwHalf, grid.position.x + grid.getWidth() - vwHalf);
        camera.position.y = MathUtils.clamp(camera.position.y,
            grid.position.y + vhHalf, grid.position.y + grid.getHeight() - vhHalf);
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

        // Invoke mouse movement event if camera is moved
        if (cameraVelocity.x != 0f || cameraVelocity.y != 0f) {
            mouseMoved(mouseScreenX, mouseScreenY);
        }

        setCameraPosition(camera.position.x + cameraVelocity.x, camera.position.y + cameraVelocity.y);
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
        dronePortTexture.dispose();
        wasteDisposalTexture.dispose();
        itemTexture.dispose();
        droneTexture.dispose();
        pathTexture.dispose();
        pathCrossTexture.dispose();
        selectTexture.dispose();
        selectMineTexture.dispose();
        selectFactoryTexture.dispose();
        selectStorageTexture.dispose();
        selectDronePortTexture.dispose();
        selectWasteDisposalTexture.dispose();
        selectFromTexture.dispose();
        selectToTexture.dispose();
        removeTexture.dispose();
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
        // Create the actor instance
        Coordinate location = building.getLocation();
        Vector2 worldPos = coordinateToWorld(location);
        BuildingActor actor = new BuildingActor(building, animation, removeTexture, worldPos.x, worldPos.y);

        // Add the actor
        buildingActors.add(actor);
        buildingMap.put(location, actor); // Add to building map for fast lookup
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
        if (building instanceof DronePortBuilding) {
            return actorizeBuilding(building, dronePortAnimation);
        }
        if (building instanceof WasteDisposalBuilding) {
            return actorizeBuilding(building, wasteDisposalAnimation);
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
        StorageBuilding storage = new StorageBuilding(name, new ArrayList<>(), sim, storageItem, maxCapacity, priority);
        return buildBuilding(storage, storageAnimation, coordinate);
    }

    public BuildingActor buildDronePort(String name, Coordinate coordinate) {
        name = sim.getWorld().resolveBuildingNameConflict(name);
        DronePortBuilding dronePort = new DronePortBuilding(name, Collections.emptyList(), sim);
        return buildBuilding(dronePort, dronePortAnimation, coordinate);
    }

    public BuildingActor buildWasteDisposal(String name,
                                            LinkedHashMap<Item, Integer> wasteTypes,
                                            LinkedHashMap<Item, Integer> disposalRateMaps,
                                            LinkedHashMap<Item, Integer> timeSteps,
                                            Coordinate coordinate) {
        name = sim.getWorld().resolveBuildingNameConflict(name);
        WasteDisposalBuilding wasteDisposal =
            new WasteDisposalBuilding(name, wasteTypes, disposalRateMaps, timeSteps, sim);
        return buildBuilding(wasteDisposal, wasteDisposalAnimation, coordinate);
    }

    /**
     * Asks to remove a building and its actor.
     *
     * @param buildingActor is the building actor to remove.
     */
    public void removeBuilding(BuildingActor buildingActor) {
        sim.removeBuilding(buildingActor.getBuilding());
    }

    /**
     * The event listener being called when a building is fully removed.
     *
     * @param building is the building that got fully removed.
     * @throws IllegalArgumentException when the building is not an actor in the game world.
     */
    private void onBuildingRemoved(Building building) {
        // Find its actor
        BuildingActor actor = null;
        for (BuildingActor a : buildingActors) {
            if (a.getBuilding() == building) {
                actor = a;
                break;
            }
        }
        if (actor == null) {
            throw new IllegalArgumentException("The building does not have an actor in the game world");
        }

        // Demolish the building (delete its actor)
        demolishBuilding(actor);
    }

    /**
     * Completely remove a building's actor.
     *
     * @param buildingActor is the building actor to remove.
     */
    private void demolishBuilding(BuildingActor buildingActor) {
        // Remove building actor
        buildingMap.remove(buildingActor.getBuilding().getLocation());
        buildingActors.remove(buildingActor);

        // Collect paths
        List<PathEntry> toRemove = new ArrayList<>();
        for (PathEntry entry : pathEntries) {
            if (entry.from == buildingActor || entry.to == buildingActor) {
                toRemove.add(entry);
            }
        }

        // Disconnect
        for (PathEntry entry : toRemove) {
            disconnectPath(entry.from, entry.to);
            disconnectPath(entry.to, entry.from);
        }
    }

    /**
     * Takes an existing path and creates an actor from it.
     *
     * @param path is the path instance to be an actor.
     * @param from is the source building actor.
     * @param to is the destination building actor.
     * @return constructed `PathActor` instance.
     */
    private PathActor actorizePath(Path path, BuildingActor from, BuildingActor to) {
        // Create actor
        PathActor actor = new PathActor(path, sim.getWorld().getTileMap(), pathAnimator, pathCrossTexture,
            this::coordinateToWorld);
        pathEntries.add(new PathEntry(actor, path, from, to));

        // Cache and sort paths
        for (Coordinate c : actor.getCrossCoordinates()) {
            pathCrossCoords.add(c);
        }
        pathCrossCoords.sort((a, b) -> {
            float ay = coordinateToWorld(a).y;
            float by = coordinateToWorld(b).y;
            return Float.compare(by, ay);
        });

        return actor;
    }

    public PathActor connectPath(BuildingActor from, BuildingActor to) {
        // Prevent connecting to self
        if (from == to) {
            throw new IllegalArgumentException("Cannot connect to self");
        }

        // Connect the two buildings
        Path path = sim.connectBuildings(from.getBuilding(), to.getBuilding());
        if (path == null) {
            throw new IllegalArgumentException("Cannot connect " + from.getBuilding().getName() + " to " + to.getBuilding().getName() + ": No valid path");
        }

        // If the path is already an actor, return the actor
        for (PathEntry entry : pathEntries) {
            if (entry.path == path) {
                return entry.actor;
            }
        }

        // Create the actor
        return actorizePath(path, from, to);
    }

    public void disconnectPath(BuildingActor from, BuildingActor to) {
        // Ignore self disconnection
        if (from == to) {
            return;
        }

        // Find matching path entries
        List<PathEntry> toRemove = new ArrayList<>();
        for (PathEntry entry : pathEntries) {
            if (entry.from == from && entry.to == to) {
                toRemove.add(entry);
            }
        }
        if (toRemove.isEmpty()) {
            return;
        }

        // Try to disconnect
        try {
            sim.disconnectBuildings(from.getBuilding(), to.getBuilding());
        } catch (Exception ignored) { }

        // Remove path entries and cross coordinates
        for (PathEntry entry : toRemove) {
            pathEntries.remove(entry);
            for (Coordinate c : entry.actor.getCrossCoordinates()) {
                pathCrossCoords.remove(c);
            }
        }
    }

    /**
     * Gets the building actor at a certain coordinate.
     *
     * @param c is the coordinate to get the actor.
     * @return the building actor instance at that coordinate or null.
     */
    private BuildingActor getBuildingAt(Coordinate c) {
        return buildingMap.get(c);
    }



    public interface Phase {
        default void onLeftClick(Coordinate c) { }
        default void onRightClick(Coordinate c) { }
        default void onLeftRelease(Coordinate c) { }
        default void onRightRelease(Coordinate c) { }
        default void onEnter() { }
        default void onExit() { }
    }

    public class DefaultPhase implements Phase {
        @Override
        public void onLeftClick(Coordinate c) {
            BuildingActor actor = getBuildingAt(c);
            if (actor != null) {
                screen.showBuildingInfo(actor.getBuilding());
            }
        }

        @Override
        public void onRightClick(Coordinate c) {
            BuildingActor actor = getBuildingAt(c);
            if (actor != null) {
                removeBuilding(actor);
            }
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
        public abstract void onLeftClick(Coordinate c);

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
        public void onLeftClick(Coordinate c) {
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
        public void onLeftClick(Coordinate c) {
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
        public void onLeftClick(Coordinate c) {
            try {
                buildStorage(name, storageItem, maxCapacity, priority, c);
            } catch (Exception e) {
                log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class BuildDronePortPhase extends BuildPhase {
        public BuildDronePortPhase(String name) {
            super(selectDronePortTexture, name);
        }

        @Override
        public void onLeftClick(Coordinate c) {
            try {
                buildDronePort(name, c);
            } catch (Exception e) {
                log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class BuildWasteDisposalPhase extends BuildPhase {
        private final LinkedHashMap<Item, Integer> wasteTypes;
        private final LinkedHashMap<Item, Integer> disposalRateMaps;
        private final LinkedHashMap<Item, Integer> timeSteps;

        public BuildWasteDisposalPhase(String name,
                                       LinkedHashMap<Item, Integer> wasteTypes,
                                       LinkedHashMap<Item, Integer> disposalRateMaps,
                                       LinkedHashMap<Item, Integer> timeSteps) {
            super(selectWasteDisposalTexture, name);
            this.wasteTypes = wasteTypes;
            this.disposalRateMaps = disposalRateMaps;
            this.timeSteps = timeSteps;
        }

        @Override
        public void onLeftClick(Coordinate c) {
            try {
                buildWasteDisposal(name, wasteTypes, disposalRateMaps, timeSteps, c);
            } catch (Exception e) {
                log(e.getMessage());
            } finally {
                enterDefaultPhase();
            }
        }
    }

    public class ConnectPhase implements Phase {
        private BuildingActor from = null;
        private boolean isConnecting = true;

        private boolean pressedLeft = true;
        private boolean buttonPressed = false;

        @Override
        public void onLeftClick(Coordinate c) {
            handleClick(c, true);
        }

        @Override
        public void onRightClick(Coordinate c) {
            handleClick(c, false);
        }

        private void handleClick(Coordinate c, boolean connect) {
            try {
                pressedLeft = connect;
                buttonPressed = true;

                if (isConnecting != connect) {
                    from = null;
                    isConnecting = connect;
                    grid.setSelectColor(connect ? Color.WHITE : Color.RED);
                }

                if (from == null) {
                    from = getBuildingAt(c);
                    if (from != null) {
                        grid.setSelectTexture(selectToTexture);
                    }
                    return;
                }

                performAction(c);
            } catch (Exception e) {
                log(e.getMessage());
                resetState();
            }
        }

        @Override
        public void onLeftRelease(Coordinate c) {
            handleRelease(c, true);
        }

        @Override
        public void onRightRelease(Coordinate c) {
            handleRelease(c, false);
        }

        private void handleRelease(Coordinate c, boolean releasedLeft) {
            try {
                if (!buttonPressed || pressedLeft != releasedLeft) {
                    return;
                }

                if (from == null || from.getBuilding().getLocation().equals(c)) {
                    return;
                }

                performAction(c);
                grid.setSelectColor(Color.WHITE);
            } catch (Exception e) {
                log(e.getMessage());
                resetState();
            } finally {
                buttonPressed = false;
            }
        }

        private void performAction(Coordinate c) {
            BuildingActor to = getBuildingAt(c);
            if (to == null) {
                return;
            }

            if (isConnecting) {
                connectPath(from, to);
            } else {
                disconnectPath(from, to);
            }

            enterDefaultPhase();
        }

        private void resetState() {
            from = null;
            isConnecting = true;
            buttonPressed = false;
            grid.setSelectColor(Color.WHITE);
            enterDefaultPhase();
        }

        @Override
        public void onEnter() {
            grid.setSelectTexture(selectFromTexture);
        }

        @Override
        public void onExit() {
            grid.setSelectColor(Color.WHITE);
            grid.setSelectTexture(selectTexture);
        }
    }

    private Phase phase = new DefaultPhase();

    public void enterDefaultPhase() {
        phase.onExit();
        phase = new DefaultPhase();
        phase.onEnter();
    }

    public void enterConnectPhase() {
        phase.onExit();
        phase = new ConnectPhase();
        phase.onEnter();
    }

    public void enterBuildMinePhase(String name, Recipe miningRecipe) {
        phase.onExit();
        phase = new BuildMinePhase(name, miningRecipe);
        phase.onEnter();
    }

    public void enterBuildFactoryPhase(String name, Type factoryType) {
        phase.onExit();
        phase = new BuildFactoryPhase(name, factoryType);
        phase.onEnter();
    }

    public void enterBuildStoragePhase(String name, Item storageItem, int maxCapacity, double priority) {
        phase.onExit();
        phase = new BuildStoragePhase(name, storageItem, maxCapacity, priority);
        phase.onEnter();
    }

    public void enterBuildDronePortPhase(String name) {
        phase.onExit();
        phase = new BuildDronePortPhase(name);
        phase.onEnter();
    }

    public void enterBuildWasteDisposalPhase(String name,
                                             LinkedHashMap<Item, Integer> wasteTypes,
                                             LinkedHashMap<Item, Integer> disposalRateMaps,
                                             LinkedHashMap<Item, Integer> timeSteps) {
        phase.onExit();
        phase = new BuildWasteDisposalPhase(name, wasteTypes, disposalRateMaps, timeSteps);
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
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            // Get coordinate based on touch screen position
            Vector2 pos = screenToWorld(new Vector2(screenX, screenY));
            Coordinate c = worldToCoordinate(pos);

            // Invoke current phase's `onClick` event
            if (button == Input.Buttons.LEFT) {
                phase.onLeftClick(c);
            } else {
                phase.onRightClick(c);
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            // Get coordinate based on touch screen position
            Vector2 pos = screenToWorld(new Vector2(screenX, screenY));
            Coordinate c = worldToCoordinate(pos);

            // Invoke current phase's `onRelease` event
            if (button == Input.Buttons.LEFT) {
                phase.onLeftRelease(c);
            } else {
                phase.onRightRelease(c);
            }
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
        if (delivery instanceof DroneDelivery droneDelivery) {
            droneDeliveries.add(new DroneDeliveryActor(droneDelivery, droneAnimation, this::coordinateToWorld));
        } else {
            deliveries.add(new DeliveryActor(delivery, itemTexture, this::coordinateToWorld));
        }
    }

    @Override
    public void onDeliveryFinished(Delivery delivery) {
        if (delivery instanceof DroneDelivery droneDelivery) {
            droneDeliveries.removeIf((actor) -> actor.getDroneDelivery() == droneDelivery);
        }
    }
}
