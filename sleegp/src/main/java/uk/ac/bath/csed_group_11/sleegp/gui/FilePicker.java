package uk.ac.bath.csed_group_11.sleegp.gui;

import java.io.File;
import java.util.List;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author mathew
 * Source from one of my previous projects: https://github.com/Mallington/OHR/blob/master/src/GUI/FilePicker.java
 */
public class FilePicker {

    /**
     * Value to be specified function for the save function
     */
    public static int SAVE = 0;
    /**
     * Value to be specified function for the open function
     */
    public static int OPEN = 1;

    private FileChooser PICKER;
    /**
     * Allows for custom file types to be used
     */
    private String FILE_TYPE;

    /**
     *
     * @param fileType eg .nns, .fs, .png
     * @param descriptor This is the syntax used to describe the file type for
     * example .txt would be Text File
     * @param defaultName For example untitled
     */
    public FilePicker(String fileType, String descriptor, String defaultName) {
        PICKER = new FileChooser();
        PICKER.getExtensionFilters().add(new ExtensionFilter(descriptor + " (" + fileType + ")", "*" + fileType));
        PICKER.setInitialFileName(defaultName + fileType);
        FILE_TYPE = fileType;
    }

    /**
     * Alternative instantiation without the default filename
     */
    public FilePicker(String descriptor, List<String> fileTypes) {
        PICKER = new FileChooser();
        for (String fileType : fileTypes) {
            PICKER.getExtensionFilters().add(new ExtensionFilter(descriptor + " (" + fileType + ")", "*" + fileType));
        }

    }

    /**
     *
     * @param s The current GUI it is being opened from
     * @param option Specifies the either LOAD or SAVE on the file picker UI
     * @return Returns the file
     */
    public File getFile(Stage s, int option) {
        File f = null;
        if (option == SAVE) {
            f = PICKER.showSaveDialog(s);
            if (!f.getName().contains(FILE_TYPE)) {
                f = new File(f.getAbsoluteFile() + FILE_TYPE);
            }
        } else if (option == OPEN) {
            f = PICKER.showOpenDialog(s);
        } else {
            f = null;
            System.out.println("Not a valid option!");
        }

        return f;

    }

    public static void main(String[] args) {
        FilePicker fp = new FilePicker(".nns", "neural net struct", "Unitled");
    }
}
