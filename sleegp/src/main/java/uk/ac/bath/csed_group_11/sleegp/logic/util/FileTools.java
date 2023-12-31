package uk.ac.bath.csed_group_11.sleegp.logic.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for reading, writing text file
 *
 * @author mathew
 */
public class FileTools {

    public static boolean saveObject(Object toSave, File firectory, String extension) throws FileNotFoundException {
        if (firectory.toString().contains(extension)) {
            Save saver = new Save(firectory);
            saver.write(toSave);
        } else {
            System.out.println("Cannot save without proper extenson: " + extension);
        }
        return false;
    }

    /**
     * Writes a new entry to a new or existing text file
     *
     * @param dir     Directory to be written to
     * @param toWrite Text to be written on a new line
     * @throws IOException
     */
    public static void write(String dir, String toWrite) throws IOException {
        FileWriter fw = new FileWriter(dir, true);
        fw.write(toWrite + "\n");
        fw.close();
    }

    /**
     * Reads the contents of a text file
     *
     * @param dir Directory of the text file
     * @return returns the text written
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String read(String dir) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(dir);
        String append = "";
        while (inputStream.available() > 0) {
            append += (char) inputStream.read();
        }
        inputStream.close();
        return append;
    }

    /**
     * Separates a CSV file into an list for easy processing
     *
     * @param contents
     * @return
     */
    public List<String> separateCommaValues(String contents) {
        String append = "";
        List<String> ret = new ArrayList<String>();
        for (int j = 0; j < contents.length(); j++) {
            char c;
            if ((c = contents.charAt(j)) == ',') {
                ret.add(append);
                append = "";
            } else {
                append += c;
            }

        }
        return ret;
    }
}
