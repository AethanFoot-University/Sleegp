/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import Util.Load;
import Util.Save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathew Allington
 */
public class EpochContainer implements Serializable {
    private List<Epoch> data;
    public final String EXTENSION = ".ec";

    /**
     * Creates an empty container instance
     */
    public EpochContainer() {
        data = new ArrayList<Epoch>();
    }

    /**
     * @param file
     */
    public static EpochContainer loadContainerFromFile(File file) throws IOException {
        Load loader = new Load(file);
        Object loaded = loader.load();

        if(loaded.getClass().equals(EpochContainer.class)){
            return (EpochContainer)loaded;
        }
        else{
            return null;
        }

    }

    /**
     * @return
     */
    public int size() {
        return data.size();
    }

    /**
     * @param id
     * @return
     */
    public Epoch getEpoch(int id) {
        return data.get(id);
    }

    /**
     * @param e
     */
    public void addEpoch(Epoch e) {
        data.add(e);
    }

    /**
     * @param firectory
     * @return
     */
    public boolean saveToFile(File firectory) throws FileNotFoundException {
        if(firectory.toString().contains(EXTENSION)) {
            Save saver = new Save(firectory);
            saver.write(this);
        }
        else{
            System.out.println("Cannot save without proper extenson: "+EXTENSION);
        }
        return false;
    }
}
