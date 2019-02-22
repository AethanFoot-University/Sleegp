package uk.ac.bath.csed_group_11.logic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class is used for writing objects straight to a specified directory
 *
 * @author mathew
 */
public class Save {

    /**
     * File to be saved to
     */
    private File FILE;

    /**
     * Initialises with the directory to be written to
     *
     * @param loc
     * @throws FileNotFoundException
     */
    public Save(File loc) throws FileNotFoundException {
        FILE = loc;
    }

    /**
     * Writes an object to the pre-defined directory
     *
     * @param toWrite Object to write
     * @return true - Successfully Written, false - failed to write
     * @throws IOException Throws exception when file could not be written
     */
    public boolean write(Object toWrite){
        try {
            Files.write(FILE.toPath(), ObjectConverter.serialize(toWrite));

            return true;
        } catch (Exception e) {

            System.out.println("Failed to write object");
            e.printStackTrace();
            return false;
        }

    }
}
