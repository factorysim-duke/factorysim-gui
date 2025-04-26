package edu.duke.ece651.factorysim.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import edu.duke.ece651.factorysim.AppWrapper;
import edu.duke.ece651.factorysim.Constants;
import edu.duke.ece651.factorysim.FactoryGame;
import java.io.IOException;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            if (!(args[0].equals("-nw")) || args.length == 1) {
                System.err.println("Usage: app");
                System.err.println("       app -nw <file_path>");
                System.err.println("       app -nw <host> <port> <preset_path>");
                System.err.println("       app -nw <host> <port> <username> <password>");
                System.exit(1);
                return;
            }

            String[] wrappedArgs = new String[args.length - 1];
            System.arraycopy(args, 1, wrappedArgs, 0, wrappedArgs.length);
            AppWrapper.mainWrapper(wrappedArgs);
            return;
        }

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new FactoryGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("factorysim-gui");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        configuration.setWindowSizeLimits(640, 360, -1, -1);  // min width=600, min height=600, max width/height unlimited
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
