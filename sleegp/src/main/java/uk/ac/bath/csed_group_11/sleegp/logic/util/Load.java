package uk.ac.bath.csed_group_11.sleegp.logic.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public Load(File loc) {
        file = loc;

    }

    /**
     * Loads an object based on the predefined directory
     *
     * @return
     */
    public Object load() throws IOException, ClassNotFoundException{
        byte[] array = Files.readAllBytes(file.toPath());
        return ObjectConverter.deserialize(array);
    }

}
