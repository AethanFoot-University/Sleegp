package uk.ac.bath.csed_group_11.sleegp.logic.data;

import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Load;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends ArrayList<DataCouple> implements Serializable {
    /**
     * This is serializable ID, set to static so that there is not an ID mis-match error when saving files
     */
    static final long serialVersionUID = -2183068004576095519L;

    private int points = 0;

    public static String EXTENSION = ".usr";

    public boolean saveToFile(File firectory) throws FileNotFoundException {
        return FileTools.saveObject(this, firectory, EXTENSION);
    }

    public void setPoints(int points){
        this.points+=points;
    }

    public int returnPoints(){
        return points;
    }

    public static User loadUserFromFile(File file) throws IOException,
        ClassNotFoundException {
        Load loader = new Load(file);
        Object loaded = loader.load();

        return (User)loaded;
    }
}
