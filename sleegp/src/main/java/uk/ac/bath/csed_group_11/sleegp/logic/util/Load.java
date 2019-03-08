package uk.ac.bath.csed_group_11.sleegp.logic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

/**
 * This is used to load an object from a file
 *
 * @author mathew
 */
public class Load {

    /**
     * Directory of file/object to be read
     */
    private File file;

    public Load(File loc) throws FileNotFoundException {
        file = loc;

    }

    /**
     * Loads an object based on the predefined directory
     *
     * @return
     */
    public Object load() {
        try {
            byte[] array = Files.readAllBytes(file.toPath());

            return ObjectConverter.deserialize(array);
        } catch (Exception e) {

            return null;
        }

    }

}
