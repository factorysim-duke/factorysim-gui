package edu.duke.ece651.factorysim.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;

import edu.duke.ece651.factorysim.screen.SimulationScreen;

public class FileDialogUtil {

    /**
     * Creates and returns a configured FileChooser for loading simulations.
     *
     * @param simScreen is the SimulationScreen instance for which simulations should be loaded
     * @return a fully configured FileChooser
     */
    public static FileChooser createFileChooser(final SimulationScreen simScreen) {
        // If you want to set a custom favorites preferences name:
        FileChooser.setDefaultPrefsName("edu.duke.ece651.factorysim.filechooser");

        FileChooser fileChooser = new FileChooser(Mode.OPEN);

        // Create and set up file type filter
        FileTypeFilter typeFilter = new FileTypeFilter(true);
        typeFilter.addRule("Simulation files (*.json)", "json");
        fileChooser.setFileTypeFilter(typeFilter);

        // Set other chooser properties
        fileChooser.setSelectionMode(SelectionMode.FILES);

        // Define what happens when a user selects a file
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    String jsonPath = files.first().file().getAbsolutePath();
                    try {
                        simScreen.loadSimulation(jsonPath);
                        System.out.println("Simulation loaded from: " + jsonPath);
                    } catch (Exception e) {
                        System.err.println("Failed to load simulation: " + e.getMessage());
                    }
                }
            }
        });

        return fileChooser;
    }

    public static FileChooser saveFileChooser(final SimulationScreen simScreen) {
        FileChooser.setDefaultPrefsName("edu.duke.ece651.factorysim.filechooser");

        FileChooser fileChooser = new FileChooser(Mode.SAVE);

        FileTypeFilter typeFilter = new FileTypeFilter(true);
        typeFilter.addRule("Simulation files (*.json)", "json");
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setSelectionMode(SelectionMode.FILES);

        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                if (files.size > 0) {
                    String jsonPath = files.first().file().getAbsolutePath();
                    try {
                        simScreen.saveSimulation(jsonPath);
                        System.out.println("Simulation saved to: " + jsonPath);
                    } catch (Exception e) {
                        System.err.println("Failed to save simulation: " + e.getMessage());
                    }
                }
            }
        });

        return fileChooser;
    }

}
