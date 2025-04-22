package edu.duke.ece651.factorysim;

/**
 * Stores useful constants.
 */
public class Constants {
    private Constants() { }

    // Window dimensions
    public static final int WINDOW_WIDTH = 1600;
    public static final int WINDOW_HEIGHT = 900;

    // Default view dimensions
    public static final int DEFAULT_VIEW_WIDTH = 640;
    public static final int DEFAULT_VIEW_HEIGHT = 360;

    // Default cell size
    public static final int DEFAULT_CELL_SIZE = 16;

    // Current view dimensions and cell size - can be changed by settings
    public static int VIEW_WIDTH = DEFAULT_VIEW_WIDTH;
    public static int VIEW_HEIGHT = DEFAULT_VIEW_HEIGHT;
    public static int CELL_SIZE = DEFAULT_CELL_SIZE;

    // Update view dimensions
    public static void setViewDimensions(int width, int height) {
        VIEW_WIDTH = width;
        VIEW_HEIGHT = height;
    }

    // Update cell size
    public static void setCellSize(int size) {
        CELL_SIZE = size;
    }
}