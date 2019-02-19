/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

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
    private File FILE;

    public Load(File loc) throws FileNotFoundException {
        FILE = loc;

    }

    /**
     * Loads an object based on the predefined directory
     *
     * @return
     * @throws IOException
     */
    public Object load() throws IOException {
        try {

            byte[] array = Files.readAllBytes(FILE.toPath());
            System.out.println("Arr " + array.length);

            return Util.ObjectConverter.deserialize(array);
        } catch (Exception e) {

            return null;
        }

    }

}